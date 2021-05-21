package com.example.weatherapp

class Weather {
    var location: Location? = null
    var currentCondition: CurrentCondition = CurrentCondition() //tạo mới 1 đối tượng

    var temperature: Temperature = Temperature()
    var wind: Wind = Wind()
    var rain: Rain = Rain()
    var snow: Snow = Snow()
    var clouds: Clouds = Clouds()

    lateinit var iconData: ByteArray

    class CurrentCondition {
        var weatherId = 0
        var condition: String? = null
        var descr: String? = null
        var icon: String? = null
        var pressure = 0f
        var humidity = 0f
    }

    class Temperature {
        var temp = 0f
        var minTemp = 0f
        var maxTemp = 0f
    }

    class Wind {
        var speed = 0f
        var deg = 0f
    }
    class Rain {
        var time: String? = null
        var ammount = 0f
    }

    class Snow {
        var time: String? = null
        var ammount = 0f
    }

    class Clouds {
        var perc = 0
    }
}