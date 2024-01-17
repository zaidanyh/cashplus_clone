package com.pasukanlangit.id.kyc_domain.domain.model

data class EKycProfileUser(
    val full_name : String,
    val address : String,
    val prov : String,
    val city : String,
    val district : String,
    val village : String,
    val zipcode : String,
    val nik: String,
    val email : String,
    val img_url : String,
    val phones: List<UserPhone>?,
    val owner_name: String,
    val placeOfBorn: String,
    val dateOfBirth: String
)

data class UserPhone (
    val phone : String
)
