package com.jakubn.codequizapp.data.api

import com.jakubn.codequizapp.domain.model.Question
import retrofit2.Call
import retrofit2.http.GET

interface QuizApiService {


    @GET("questions")
    fun getAllData(): Call<Question>

}