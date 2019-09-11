package wen.jie.currencyconversion

class Config {
    companion object {
        lateinit var instance: Config

        const val ACCESS_KEY = "22521629e8cf098c95533a05553ffa1d"
        const val NETWORK_TIMEOUT = 15L
        private const val BASE_API_DOMAIN = "apilayer.net/api/"
        const val BASE_API_URL = "http://$BASE_API_DOMAIN"
    }
}