package com.example.restaurantreview

import java.io.Serializable


/*
 * Data model class to store restaurant attributes
 */
data class User(var user_name:String?=null,
                       var user_image:String?=null,
                        var user_ID: String?=null
                       ): Serializable
