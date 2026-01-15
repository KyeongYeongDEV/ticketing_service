package com.example.ticketing_service.concert.presentation.dto

import com.example.ticketing_service.concert.domain.Concert


data class ConcertResponse(
    val id : Long,
    val title : String,
    val description: String
) {
     companion object {
         fun from(concert : Concert) : ConcertResponse {
             return ConcertResponse (
                 id = concert.id!!,
                 title = concert.title,
                 description =  concert.description
             )
         }
     }
}
