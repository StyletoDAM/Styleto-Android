package tn.esprit.labasniandroid.models.services

import tn.esprit.labasniandroid.models.entities.CloudinaryImage
import tn.esprit.labasniandroid.models.repositories.CloudinaryRepository

class CloudinaryGalleryService(
    private val repository: CloudinaryRepository = CloudinaryRepository()
) {
    suspend fun loadGallery(): Result<List<CloudinaryImage>> = repository.fetchGallery()
}

