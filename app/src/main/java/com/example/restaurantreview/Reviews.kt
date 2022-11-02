package com.example.restaurantreview

import java.io.Serializable


/*
 * Data model class to store restaurant attributes
 */
data class Review(var user_ID:String?=null,
                   var meal_image:String?=null,
                   var review_description: String?=null,
                   var date_edited: String?=null,
                   var location: String?=null,
                    var res_ID: String?=null,
                  var review_ID:String?=null): Serializable

