package com.sam.fetchweatherperiodically.model.dto

data class WeatherResponse(
    val current: Current,
    val location: Location
)