package com.example.lab3.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Restaurant @JsonCreator constructor(
    @JsonProperty("id") val id: Long = 0,
    @JsonProperty("name") val name: String,
    @JsonProperty("address") val address: String
)
