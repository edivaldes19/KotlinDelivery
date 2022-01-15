package com.manuel.delivery.utils

import android.content.Context
import com.google.gson.Gson
import com.manuel.delivery.models.User

object Constants {
    fun getUserInSession(context: Context): User? {
        val mySharedPreferences = MySharedPreferences(context)
        if (!mySharedPreferences.getData(PROP_USER).isNullOrEmpty()) {
            return Gson().fromJson(
                mySharedPreferences.getData(PROP_USER),
                User::class.java
            )
        }
        return null
    }

    const val PERMISSION_ID = 1
    const val REQUEST_PHONE_CALL = 2
    const val API_URL = "http://192.168.0.13:3000/api/"
    const val ROUTE_USERS_CREATE = "users/create"
    const val ROUTE_USERS_LOGIN = "users/login"
    const val ROUTE_USERS_UPDATE = "users/update"
    const val ROUTE_USERS_UPDATE_WITHOUT_IMAGE = "users/updateWithoutImage"
    const val ROUTE_USERS_UPDATE_NOTIFICATION_TOKEN = "users/updateNotificationToken"
    const val ROUTE_USERS_FIND_DELIVERY_MAN = "users/findDeliveryMan"
    const val ROUTE_PRODUCTS_CREATE = "products/create"
    const val ROUTE_CATEGORIES_CREATE = "categories/create"
    const val ROUTE_CATEGORIES_GET_ALL = "categories/getAll"
    const val ROUTE_ADDRESS_FIND_BY_USER = "address/findByUser/{id_user}"
    const val ROUTE_ORDERS_FIND_BY_STATUS = "orders/findByStatus/{status}"
    const val ROUTE_ORDERS_FIND_BY_ID_CLIENT_AND_STATUS =
        "orders/findByClientAndStatus/{id_client}/{status}"
    const val ROUTE_ORDERS_FIND_BY_ID_DELIVERY_AND_STATUS =
        "orders/findByDeliveryAndStatus/{id_delivery}/{status}"
    const val ROUTE_ORDERS_CREATE = "orders/create"
    const val ROUTE_ORDERS_UPGRADE_TO_READY = "orders/upgradeToReady"
    const val ROUTE_ORDERS_UPGRADE_TO_ON_THE_WAY = "orders/upgradeToOnTheWay"
    const val ROUTE_ORDERS_UPGRADE_TO_DELIVERED = "orders/upgradeToDelivered"
    const val ROUTE_ORDERS_UPDATE_LATITUDE_AND_LONGITUDE = "orders/updateLatitudeAndLongitude"
    const val ROUTE_ADDRESS_CREATE = "address/create"
    const val ROUTE_PRODUCTS_FIND_BY_CATEGORY = "products/findByCategory/{id_category}"
    const val SOCKET_ROUTE = "http://192.168.0.13:3000/orders/delivery"
    const val PROP_ID = "id"
    const val PROP_ID_USER = "id_user"
    const val PROP_EMAIL = "email"
    const val PROP_NAME = "name"
    const val PROP_DESCRIPTION = "description"
    const val PROP_PRICE = "price"
    const val PROP_LASTNAME = "lastname"
    const val PROP_PHONE = "phone"
    const val PROP_IMAGE = "image"
    const val PROP_PASSWORD = "password"
    const val PROP_IS_AVAILABLE = "is_available"
    const val PROP_SESSION_TOKEN = "session_token"
    const val PROP_NOTIFICATION_TOKEN = "notification_token"
    const val PROP_SUCCESS = "success"
    const val PROP_MESSAGE = "message"
    const val PROP_DATA = "data"
    const val PROP_ERROR = "error"
    const val PROP_ROUTE = "route"
    const val PROP_USER = "user"
    const val PROP_AMOUNT = "amount"
    const val PROP_CATEGORY = "category"
    const val PROP_ID_CATEGORY = "id_category"
    const val PROP_NAME_CATEGORY = "category_name"
    const val PROP_PRODUCT = "product"
    const val PROP_ORDER = "order"
    const val PROP_ROLE = "role"
    const val PROP_CITY = "city"
    const val PROP_COUNTRY = "country"
    const val PROP_ADDRESS = "address"
    const val PROP_LATITUDE = "latitude"
    const val PROP_LONGITUDE = "longitude"
    const val PROP_SUBURB = "suburb"
    const val PROP_ID_CLIENT = "id_client"
    const val PROP_ID_DELIVERY = "id_delivery"
    const val PROP_ID_ADDRESS = "id_address"
    const val PROP_CLIENT = "client"
    const val PROP_DELIVERY = "delivery"
    const val PROP_STATUS = "status"
    const val PROP_TIMESTAMP = "timestamp"
    const val PROP_SWITCH_KEY = "switch_key"
    const val PROP_RADIO_KEY = "radio_key"
    const val PROP_POSITION = "position"
    const val PROP_TITLE = "title"
    const val PROP_BODY = "body"
    const val PROP_NOTIFICATION_ID = "notificationId"
    const val PROP_IMAGE_CREATE = "image/*"
    const val PROP_TEXT_PLAIN = "text/plain"
    const val PROP_AUTHORIZATION = "Authorization"
}