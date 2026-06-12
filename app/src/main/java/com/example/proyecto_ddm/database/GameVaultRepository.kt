package com.example.proyecto_ddm.database

import android.content.Context
import com.example.proyecto_ddm.database.entities.CartDetailEntity
import com.example.proyecto_ddm.database.entities.CartEntity
import com.example.proyecto_ddm.database.entities.FavoriteEntity
import com.example.proyecto_ddm.database.entities.ProductEntity
import com.example.proyecto_ddm.database.entities.StateEntity
import com.example.proyecto_ddm.database.entities.UserEntity
import com.example.proyecto_ddm.models.Cart
import com.example.proyecto_ddm.models.CartItem
import com.example.proyecto_ddm.models.Category
import com.example.proyecto_ddm.models.FlatPurchaseItem
import com.example.proyecto_ddm.models.Product
import com.example.proyecto_ddm.models.State
import com.example.proyecto_ddm.models.UserStats

class GameVaultRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val productDao = db.productDao()
    private val categoryDao = db.categoryDao()
    private val stateDao = db.stateDao()
    private val cartDao = db.cartDao()
    private val favoriteDao = db.favoriteDao()

    suspend fun login(email: String, password: String): UserEntity? = userDao.login(email, password)
    suspend fun register(name: String, email: String, password: String): Long {
        val exists = userDao.getByEmail(email) != null
        if(exists) return -1L

        return try {
            userDao.insert(UserEntity(
                name = name,
                email = email,
                password = password,
                rol_id = 2
            ))
        } catch(e: Exception) {
            -2L
        }
    }

    suspend fun getUserById(id: Int): UserEntity? = userDao.getById(id)
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getByEmail(email)
    suspend fun updatePasswordById(id: Int, newPassword: String) = userDao.updatePasswordById(id, newPassword)
    suspend fun updateProfile(id: Int, name: String, email: String, imgPath: String?) =
        userDao.updateProfile(id, name, email, imgPath)

    suspend fun isAdmin(userId: Int): Boolean {
        val user = userDao.getById(userId) ?: return false
        return user.rol_id == 1
    }

    suspend fun getUserStats(userId: Int): UserStats {
        val purchases = cartDao.getCompletedCarts(userId).size
        val cartItems = getCartItems(userId).size
        val favorites = favoriteDao.countByUser(userId)
        return UserStats(purchases, cartItems, favorites)
    }

    suspend fun insertProduct(
        name: String, categoryId: Int, description: String, price: Float,
        stock: Int, imgPath: String?, adminId: Int
    ): Long {
        val existing = productDao.getByNameAndCategory(name, categoryId)

        return if(existing != null) {
            productDao.incrementStock(existing.product_id, stock)
            existing.product_id.toLong()
        } else
            productDao.insert(ProductEntity(
                name = name,
                category_id = categoryId,
                description = description,
                price = price,
                stock = stock,
                img_path = imgPath,
                id_admin = adminId
            ))
    }

    suspend fun getAllProducts(): List<Product> = productDao.getAll().mapToModel()
    suspend fun getAvailableProducts(): List<Product> = productDao.getAvailable().mapToModel()
    suspend fun getProductById(id: Int): Product? =
        productDao.getById(id)?.let { entity ->
            val cat = categoryDao.getById(entity.category_id)
            entity.toModel(cat?.name ?: "")
        }

    suspend fun getAllCategories(): List<Category> =
        categoryDao.getAll().map { Category(it.category_id, it.name) }

    private suspend fun List<ProductEntity>.mapToModel(): List<Product> =
        map { entity ->
            val cat = categoryDao.getById(entity.category_id)
            entity.toModel(cat?.name ?: "")
        }

    private fun ProductEntity.toModel(catName: String) = Product(
        id = product_id,
        name = name,
        category = Category(category_id, catName),
        description = description,
        price = price,
        img = img_path
    )

    suspend fun toggleFavorite(userId: Int, productId: Int): Boolean {
        val isFav = favoriteDao.isFavorite(userId, productId)
        if(isFav) favoriteDao.delete(userId, productId)
        else favoriteDao.insert(FavoriteEntity(userId, productId))
        return !isFav
    }

    suspend fun isFavorite(userId: Int, productId: Int): Boolean =
        favoriteDao.isFavorite(userId, productId)

    suspend fun getFavoriteProducts(userId: Int): List<Product> {
        val favorites = favoriteDao.getByUser(userId)
        return favorites.mapNotNull { fav -> getProductById(fav.product_id) }
    }

    suspend fun getOrCreateActiveCart(userId: Int): CartEntity {
        val existing = cartDao.getActiveCart(userId)
        if (existing != null) return existing
        val cartId = cartDao.insertCart(CartEntity(
            user_id = userId,
            state_id = 1,
            creation_date = currentDate()
        ))

        return cartDao.getCartById(cartId.toInt())!!
    }

    suspend fun addToCart(userId: Int, product: Product, qty: Int = 1): Boolean {
        return try {
            val cart   = getOrCreateActiveCart(userId)
            val existing = cartDao.getDetailByProduct(cart.cart_id, product.id)

            if(existing != null)
                cartDao.updateQuantity(existing.cart_detail_id, existing.quantity + qty)
            else
                cartDao.insertDetail(
                    CartDetailEntity(
                        cart_id = cart.cart_id,
                        product_id = product.id,
                        quantity = qty,
                        price_captured = product.price
                    )
                )

            true
        } catch(e: Exception) {
            false
        }
    }

    suspend fun getCartItems(userId: Int): List<CartItem> {
        val cart = cartDao.getActiveCart(userId) ?: return emptyList()
        return cartDetailToCartItems(cart.cart_id)
    }

    suspend fun getCartItemsByCartId(cartId: Int): List<CartItem> = cartDetailToCartItems(cartId)
    suspend fun getCartModelById(cartId: Int): Cart? {
        val cartEntity = cartDao.getCartById(cartId) ?: return null
        val stateEntity = stateDao.getById(cartEntity.state_id)
            ?: StateEntity(cartEntity.state_id, "completado")

        return Cart(
            id = cartEntity.cart_id,
            userId = cartEntity.user_id,
            state = State(stateEntity.state_id, stateEntity.name),
            creationDate = cartEntity.creation_date,
            completedDate = cartEntity.completed_date,
            items = cartDetailToCartItems(cartId)
        )
    }

    suspend fun updateCartItemQty(userId: Int, productId: Int, qty: Int) {
        val cart = cartDao.getActiveCart(userId) ?: return
        val detail = cartDao.getDetailByProduct(cart.cart_id, productId) ?: return
        cartDao.updateQuantity(detail.cart_detail_id, qty)
    }

    suspend fun removeFromCart(userId: Int, productId: Int) {
        val cart = cartDao.getActiveCart(userId) ?: return
        val detail = cartDao.getDetailByProduct(cart.cart_id, productId) ?: return
        cartDao.deleteDetail(detail.cart_detail_id)
    }

    suspend fun clearCart(userId: Int) {
        val cart = cartDao.getActiveCart(userId) ?: return
        cartDao.deleteAllDetails(cart.cart_id)
    }

    suspend fun completeCart(userId: Int, completedDate: String): Boolean {
        return try {
            val cart = cartDao.getActiveCart(userId) ?: return false
            cartDao.completeCart(cart.cart_id, completedDate)
            cartDao.getDetailsByCart(cart.cart_id).forEach { detail ->
                productDao.decrementStock(detail.product_id, detail.quantity)
            }

            true
        } catch(e: Exception) {
            false
        }
    }

    suspend fun getCompletedCarts(userId: Int): List<FlatPurchaseItem> {
        return cartDao.getCompletedCarts(userId).flatMap { cartEntity ->
            val state = stateDao.getById(cartEntity.state_id)
                ?: StateEntity(cartEntity.state_id, "completado")

            val cart = Cart(
                id = cartEntity.cart_id,
                userId = cartEntity.user_id,
                state = State(state.state_id, state.name),
                creationDate = cartEntity.creation_date,
                completedDate = cartEntity.completed_date
            )

            cartDetailToCartItems(cartEntity.cart_id).map { cartItem ->
                FlatPurchaseItem(cart, cartItem)
            }
        }
    }

    private suspend fun cartDetailToCartItems(cartId: Int): List<CartItem> =
        cartDao.getDetailsByCart(cartId).mapNotNull { detail ->
            val product = getProductById(detail.product_id) ?: return@mapNotNull null
            CartItem(product = product, quantity = detail.quantity)
        }

    private fun currentDate(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}