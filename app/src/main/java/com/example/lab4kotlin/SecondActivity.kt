package com.example.lab4kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val inputPowerSC = findViewById<EditText>(R.id.inputPowerSC)
        val inputVoltage = findViewById<EditText>(R.id.inputVoltage)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val navigateToExample7_1Button = findViewById<Button>(R.id.navigateToExample7_1Button) // Нова кнопка
        val outputResult = findViewById<TextView>(R.id.outputResult)

        calculateButton.setOnClickListener {
            val powerSC = inputPowerSC.text.toString().toDoubleOrNull() ?: 0.0 // Потужність КЗ у МВА
            val voltage = inputVoltage.text.toString().toDoubleOrNull() ?: 0.0 // Номінальна напруга у кВ

            // Сумарний опір системи
            val impedanceXC = 0.55 // Опір конденсатора
            val impedanceXT = 1.84 // Опір трансформатора
            val totalImpedance = impedanceXC + impedanceXT // Сумарний опір (2.39 Ом у прикладі)

            // Розрахунок струму КЗ без базисного прив'язування
            val shortCircuitCurrent = calculateShortCircuitCurrent(voltage, totalImpedance)

            // Розрахунок струму КЗ з урахуванням базисних значень
            val baseVoltage = 10.5 // Базисна напруга у кВ
            val basePower = 1000.0 // Базисна потужність у МВА
            val shortCircuitCurrentWithBase = calculateShortCircuitCurrentWithBase(baseVoltage, basePower, powerSC, voltage)

            // Виведення результату
            outputResult.text = """
        Струм КЗ без базисного прив'язування: ${String.format("%.2f", shortCircuitCurrent)} кА
        Струм КЗ з урахуванням базисних значень: ${String.format("%.2f", shortCircuitCurrentWithBase)} кА
    """.trimIndent()
        }

        // Дія для кнопки "До розрахунку прикладу 7.1"
        navigateToExample7_1Button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Переходимо до MainActivity
            startActivity(intent)
        }
    }

    // Метод для розрахунку струму КЗ без базисного прив'язування
    private fun calculateShortCircuitCurrent(nominalVoltage: Double, totalImpedance: Double): Double {
        return if (totalImpedance != 0.0) {
            (nominalVoltage) / (sqrt(3.0) * totalImpedance)
        } else {
            0.0
        }
    }

    // Метод для розрахунку струму КЗ з базисними значеннями
    private fun calculateShortCircuitCurrentWithBase(baseVoltage: Double, basePower: Double, powerSC: Double, voltage: Double): Double {
        return if (voltage != 0.0 && baseVoltage != 0.0) {
            val impedanceRatio = (baseVoltage * baseVoltage) / basePower
            val baseImpedance = (voltage * voltage) / powerSC
            baseImpedance / impedanceRatio
        } else {
            0.0
        }
    }
}
