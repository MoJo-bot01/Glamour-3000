package com.example.ourapplicationglamour3000.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.databinding.ActivityChartViewBinding
import com.github.mikephil.charting.components.XAxis
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChartViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupChart()
        loadChartData()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupChart() {
        binding.chart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setTouchEnabled(true)
            setPinchZoom(true)

            val xAxis = xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)

            val yAxis = axisLeft
            yAxis.setDrawGridLines(false)

            axisRight.isEnabled = false
        }
    }

    private fun loadChartData() {

        val ref = FirebaseDatabase.getInstance().reference.child("Categories").child("id")
        val ref1 = FirebaseDatabase.getInstance().reference.child("items").child("itemCategoryId")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Handle the data changes for both categories and items here
                // You can access the data using dataSnapshot as usual
                val entries = mutableListOf<BarEntry>()
                for (ds in snapshot.children) {
                    val goal = "${ds.child("categoryGoal").childrenCount}".toFloat()
                    val itemCount = "${ds.child("itemCategoryId").childrenCount}".toFloat()

                    entries.add(BarEntry(entries.size.toFloat(), itemCount / goal))
                }
                val dataSet = BarDataSet(entries, "Progress")
                dataSet.apply {
                    setDrawIcons(false)
                    setColors(Color.BLUE)
                }
                val barData = BarData(dataSet)
                val chart = binding.chart
                chart.data = barData
                chart.invalidate()
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occur during data retrieval
            }
        }
        ref1.addValueEventListener(valueEventListener)
        ref.addValueEventListener(valueEventListener)
    }

    private fun getColorArray(size: Int): IntArray {
        val colors = IntArray(size)
        val colorPalette = this.resources.getIntArray(R.array.chart_colors) // Custom color array defined in resources
        for (i in 0 until size) {
            colors[i] = colorPalette[i % colorPalette.size]
        }
        return colors
    }
}