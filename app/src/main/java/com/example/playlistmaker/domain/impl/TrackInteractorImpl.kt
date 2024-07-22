package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TracksRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun search(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                val tracks = repository.search(expression)
                consumer.consume(tracks)
            } catch (t: Throwable) {
                consumer.onFailure(t)
            }

        }
    }
}


