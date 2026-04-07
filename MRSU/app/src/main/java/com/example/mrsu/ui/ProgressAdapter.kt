package com.example.mrsu.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mrsu.R
import com.example.mrsu.network.model.DisciplineRatingPlanResponse
import com.example.mrsu.network.model.SemesterDiscipline

class ProgressAdapter(
    private val context: android.content.Context,
    private val data: List<Pair<SemesterDiscipline, DisciplineRatingPlanResponse?>>
) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Pair<SemesterDiscipline, DisciplineRatingPlanResponse?> = data[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
        val (discipline, ratingPlan) = getItem(position)

        val titleText: TextView = view.findViewById(R.id.disciplineTitle)
        val pointsText: TextView = view.findViewById(R.id.disciplinePoints)
        val controlDotsContainer: ViewGroup = view.findViewById(R.id.controlDotsContainer)

        titleText.text = discipline.title

        // Показываем итоговые баллы дисциплины
        if (ratingPlan != null) {
            val totalPoints = ratingPlan.totalPoints ?: 0f
            val maxPoints = ratingPlan.maxPoints ?: ratingPlan.ratingPlanPoints ?: 0f

            val totalPointsStr = if (totalPoints == totalPoints.toInt().toFloat()) {
                totalPoints.toInt().toString()
            } else {
                totalPoints.toString()
            }

            val maxPointsStr = if (maxPoints == maxPoints.toInt().toFloat()) {
                maxPoints.toInt().toString()
            } else {
                maxPoints.toString()
            }

            pointsText.text = "$totalPointsStr / $maxPointsStr"
            pointsText.visibility = View.VISIBLE
        } else {
            pointsText.text = "Нет данных"
            pointsText.visibility = View.VISIBLE
        }

        // Контрольные точки (рейтинг-план)
        controlDotsContainer.removeAllViews()

        val controlDots = ratingPlan?.controlDots
        if (!controlDots.isNullOrEmpty()) {
            controlDots.forEach { dot ->
                val dotView = LayoutInflater.from(context).inflate(R.layout.item_control_dot, controlDotsContainer, false)
                val dotName: TextView = dotView.findViewById(R.id.dotName)
                val dotPoints: TextView = dotView.findViewById(R.id.dotPoints)

                dotName.text = dot.name

                // Получаем баллы, если null то 0
                val earned = dot.points ?: 0f
                val max = dot.maxPoints ?: 0f

                val earnedStr = if (earned == earned.toInt().toFloat()) {
                    earned.toInt().toString()
                } else {
                    earned.toString()
                }

                val maxStr = if (max == max.toInt().toFloat()) {
                    max.toInt().toString()
                } else {
                    max.toString()
                }

                dotPoints.text = "$earnedStr / $maxStr"
                controlDotsContainer.addView(dotView)
            }
        } else {
            // Если нет контрольных точек, показываем сообщение
            val emptyView = TextView(context)
            emptyView.text = "Нет контрольных точек"
            emptyView.setTextColor(android.graphics.Color.GRAY)
            emptyView.textSize = 12f
            emptyView.setPadding(0, 8, 0, 0)
            controlDotsContainer.addView(emptyView)
        }

        return view
    }
}