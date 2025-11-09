package tn.esprit.labasniandroid.models

sealed class NetworkError : Exception() {
    data object InvalidURL : NetworkError()
    data class RequestFailed(val statusCode: Int) : NetworkError()
    data object DecodingFailed : NetworkError()
    data object NoData : NetworkError()
    data class ServerMessage(val serverMessage: String) : NetworkError()
    data class Transport(val error: Throwable) : NetworkError()

    override val message: String?
        get() = when (this) {
            is InvalidURL -> "URL invalide."
            is RequestFailed -> "La requête a échoué ($statusCode)."
            is DecodingFailed -> "Réponse invalide."
            is NoData -> "Aucune donnée reçue."
            is ServerMessage -> this.serverMessage
            is Transport -> error.message ?: "Erreur de transport."
        }
}

