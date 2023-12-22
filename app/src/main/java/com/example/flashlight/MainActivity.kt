package com.example.flashlight
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.flashlight.R
import java.lang.Math.atan2

class MainActivity : AppCompatActivity() {

    private lateinit var ringButton: Button
    private lateinit var flashlightButton: Button
    private lateinit var morseButton1: Button
    private lateinit var morseButton2: Button
    private lateinit var morseButton3: Button

    private var previousAngle: Float = 0f
    private var isRotating = false

    private var flashlightOn = false
    private var morse1On = false
    private var morse2On = false
    private var morse3On = false

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ringButton = findViewById(R.id.ringButton)
        flashlightButton = findViewById(R.id.flashlightButton)
        morseButton1 = findViewById(R.id.morseButton1)
        morseButton2 = findViewById(R.id.morseButton2)
        morseButton3 = findViewById(R.id.morseButton3)

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        handler = Handler()

        ringButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isRotating = true
                    previousAngle = getAngle(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isRotating) {
                        val newAngle = getAngle(event)
                        val rotation = newAngle - previousAngle
                        ringButton.rotation += rotation
                        previousAngle = newAngle
                        selectFunction()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    isRotating = false
                    // При отпускании кнопки проверяем, были ли функции включены, и если да - отключаем их
                    if (flashlightOn && flashlightButton.visibility != View.VISIBLE) {
                        flashlightOn = false
                        toggleFlashlight(false)
                        flashlightButton.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#2e7031"))
                    }
                    if (morse1On && morseButton1.visibility != View.VISIBLE) {
                        morse1On = false
                        stopMorse()
                        morseButton1.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#8a3115"))
                    }
                    if (morse2On && morseButton2.visibility != View.VISIBLE) {
                        morse2On = false
                        stopMorse()
                        morseButton2.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#12568c"))
                    }
                    if (morse3On && morseButton3.visibility != View.VISIBLE) {
                        morse3On = false
                        stopMorse()
                        morseButton3.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#6e6c0e"))
                    }
                }
            }
            true
        }

        flashlightButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!flashlightOn) {
                        flashlightOn = true
                        toggleFlashlight(true)
                        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#98FB98"))
                        // Здесь добавьте логику для включения фонарика
                    } else {
                        flashlightOn = false
                        toggleFlashlight(false)
                        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2e7031"))
                        // Здесь добавьте логику для выключения фонарика
                    }
                }
            }
            true
        }





        morseButton1.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!morse1On) {
                        morse1On = true
                        startMorse("SOS") // Здесь можно вызвать логику для активации функции Морзе
                        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFA07A"))
                        // Здесь добавьте логику для включения функции Морзе
                    } else {
                        morse1On = false
                        stopMorse()
                        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8a3115"))
                        // Здесь добавьте логику для выключения функции Морзе
                    }
                }
            }
            true
        }

        morseButton2.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!morse2On) {
                        morse2On = true
                        startMorse("QRZ") // Здесь можно вызвать логику для активации функции Морзе
                        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#87CEFA"))
                        // Здесь добавьте логику для включения функции Морзе
                    } else {
                        morse2On = false
                        stopMorse()
                        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#12568c"))
                        // Здесь добавьте логику для выключения функции Морзе
                    }
                }
            }
            true
        }

        morseButton3.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!morse3On) {
                        morse3On = true
                        startMorse("73") // Здесь можно вызвать логику для активации функции Морзе
                        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#fffd7a"))
                        // Здесь добавьте логику для включения функции Морзе
                    } else {
                        morse3On = false
                        stopMorse()
                        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6e6c0e"))
                        // Здесь добавьте логику для выключения функции Морзе
                    }
                }
            }
            true
        }

    }

    private fun getAngle(event: MotionEvent): Float {
        val x = event.x - ringButton.width / 2
        val y = ringButton.height / 2 - event.y
        return Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
    }

    private fun selectFunction() {
        val rotation = (ringButton.rotation + 360) % 360

        val visibleButton = when {
            rotation in 0f..90f || rotation in 270f..360f -> flashlightButton
            rotation > 45f && rotation <= 135f -> morseButton1
            rotation > 135f && rotation <= 225f -> morseButton2
            else -> morseButton3
        }

        flashlightButton.visibility = if (flashlightButton == visibleButton) View.VISIBLE else View.INVISIBLE
        morseButton1.visibility = if (morseButton1 == visibleButton) View.VISIBLE else View.INVISIBLE
        morseButton2.visibility = if (morseButton2 == visibleButton) View.VISIBLE else View.INVISIBLE
        morseButton3.visibility = if (morseButton3 == visibleButton) View.VISIBLE else View.INVISIBLE

        // При вращении, если функции включены и они теперь не видны на кольце, отключаем их
        if (flashlightOn && flashlightButton.visibility != View.VISIBLE) {
            flashlightOn = false
            toggleFlashlight(false)
            flashlightButton.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#2e7031"))
        }
        if (morse1On && morseButton1.visibility != View.VISIBLE) {
            morse1On = false
            stopMorse()
            morseButton1.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#8a3115"))
        }
        if (morse2On && morseButton2.visibility != View.VISIBLE) {
            morse2On = false
            stopMorse()
            morseButton2.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#12568c"))
        }
        if (morse3On && morseButton3.visibility != View.VISIBLE) {
            morse3On = false
            stopMorse()
            morseButton3.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#6e6c0e"))
        }

        // Если ни одна кнопка не видна, делаем видимой хотя бы одну из них
        if (flashlightButton.visibility != View.VISIBLE &&
            morseButton1.visibility != View.VISIBLE &&
            morseButton2.visibility != View.VISIBLE &&
            morseButton3.visibility != View.VISIBLE
        ) {
            // Пример: делаем видимой кнопку фонарика
            flashlightButton.visibility = View.VISIBLE
        }
    }






    private fun startMorse(code: String) {
        val morseMap = mapOf(
            'S' to arrayOf(1, 1, 1),
            'O' to arrayOf(3, 3, 3),
            ' ' to arrayOf(0, 0, 0),
            'Q' to arrayOf(3, 3, 1, 3, 3),
            'R' to arrayOf(1, 3, 1),
            'Z' to arrayOf(3, 3, 1, 1),
            '7' to arrayOf(1, 1, 3, 3),
            '3' to arrayOf(1, 3, 3, 3)
            // Добавьте код для остальных символов
        )

        val morseSequence = mutableListOf<Int>()
        code.forEach { char ->
            morseSequence.addAll(morseMap[char] ?: error("Invalid Morse character"))
        }

        var currentIndex = 0
        val runnable = object : Runnable {
            override fun run() {
                if (morse1On) {
                    val duration = morseSequence[currentIndex] * 100L
                    toggleFlashlight(true)
                    handler.postDelayed({
                        toggleFlashlight(false)
                        if (++currentIndex < morseSequence.size) {
                            handler.postDelayed(this, duration)
                        } else {
                            currentIndex = 0
                            handler.postDelayed(this, 500L) // Пауза между последовательностями
                        }
                    }, duration)
                }

                if (morse2On) {
                    val duration = morseSequence[currentIndex] * 100L
                    toggleFlashlight(true)
                    handler.postDelayed({
                        toggleFlashlight(false)
                        if (++currentIndex < morseSequence.size) {
                            handler.postDelayed(this, duration)
                        } else {
                            currentIndex = 0
                            handler.postDelayed(this, 500L) // Пауза между последовательностями
                        }
                    }, duration)
                }

                if (morse3On) {
                    val duration = morseSequence[currentIndex] * 100L
                    toggleFlashlight(true)
                    handler.postDelayed({
                        toggleFlashlight(false)
                        if (++currentIndex < morseSequence.size) {
                            handler.postDelayed(this, duration)
                        } else {
                            currentIndex = 0
                            handler.postDelayed(this, 500L) // Пауза между последовательностями
                        }
                    }, duration)
                }
            }
        }
        handler.post(runnable)
    }



    private fun stopMorse() {
        morse1On = false
        handler.removeCallbacksAndMessages(null)
        toggleFlashlight(false)


    }

    private fun toggleFlashlight(turnOn: Boolean) {
        try {
            cameraManager.setTorchMode(cameraId, turnOn)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
