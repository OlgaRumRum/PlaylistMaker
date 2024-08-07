package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.Resource
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    private val executor = Executors.newCachedThreadPool()


    override fun search(expression: String, consumer: SearchInteractor.SearchConsumer) {
        executor.execute {
            when (val resource = repository.search(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}


