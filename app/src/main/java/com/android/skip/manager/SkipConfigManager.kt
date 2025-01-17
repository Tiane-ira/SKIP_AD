package com.android.skip.manager

import android.graphics.Rect
import com.android.skip.dataclass.PackageInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SkipConfigManager {
    private lateinit var appInfoMap: Map<String, PackageInfo>
    fun setConfig(config: String) {
        val gson = Gson()
        val appInfoList: List<PackageInfo> =
            gson.fromJson(config, object : TypeToken<List<PackageInfo>>() {}.type)
        appInfoMap = appInfoList.associateBy { it.package_name }
        handleConfig(appInfoList)
    }

    fun setConfig(appInfoList: List<PackageInfo>) {
        handleConfig(appInfoList)
    }

    private fun handleConfig(appInfoList: List<PackageInfo>) {
        val newAppInfoList = appInfoList.map { it ->
            if (it.skip_bounds is List && it.skip_bounds.isNotEmpty()) {
                it.skip_rect_list = mutableListOf()
                for (bounds in it.skip_bounds) {
                    val boundsParts = bounds.split("#")
                    if (boundsParts.size == 2) {
                        val maxXYParts = boundsParts[0].split(",")
                        val detailBoundsParts = boundsParts[1].split(",")
                        if (maxXYParts.size == 2 && detailBoundsParts.size == 4) {
                            val (maxX, maxY) = maxXYParts
                            val (boundsLeft, boundsTop, boundsRight, boundsBottom) = detailBoundsParts
                            it.skip_rect_list.add(
                                Rect(
                                    (boundsLeft.toInt() * RectManager.maxRectX / maxX.toInt() - 1),
                                    (boundsTop.toInt() * RectManager.maxRectY / maxY.toInt() - 1),
                                    (boundsRight.toInt() * RectManager.maxRectX / maxX.toInt() + 1),
                                    (boundsBottom.toInt() * RectManager.maxRectY / maxY.toInt() + 1)
                                )
                            )
                        }
                    }
                }
            }
            it
        }
        appInfoMap = newAppInfoList.associateBy { it.package_name }
    }

//    private fun handleConfig(appInfoList: List<PackageInfo>) {
//        val newAppInfoList = appInfoList.map { it->
//            if (it.skip_points is List && it.skip_points.isNotEmpty()) {
//                it.skip_rect_list = mutableListOf()
//                for (point in it.skip_points) {
//                    val pointParts = point.split(",").map { it.toFloatOrNull() }
//                    if (pointParts.size == 2 && isBetweenZeroAndOne(pointParts[0]) && isBetweenZeroAndOne(
//                            pointParts[1]
//                        )
//                    ) {
//                        val (x, y) = pointParts
//                        if (x is Float && y is Float) {
//                            it.skip_rect_list.add(RectManager.getPointRect(x, y))
//                        }
//                    }
//                }
//            }
//            it
//        }
//        appInfoMap = newAppInfoList.associateBy { it.package_name }
//    }

    fun getSkipText(packageName: String): String {
        return appInfoMap[packageName]?.skip_text ?: "跳过"
    }

    fun getSkipId(packageName: String): String {
        return appInfoMap[packageName]?.skip_id ?: "no skip id"
    }

    fun getStartPageNodeCount(packageName: String): Int? {
        return appInfoMap[packageName]?.skip_node_count
    }

    fun getStartPageActivityName(packageName: String): String? {
        return appInfoMap[packageName]?.skip_activity_name
    }

    fun getSkipRectList(packageName: String): MutableList<Rect> {
        return appInfoMap[packageName]?.skip_rect_list ?: mutableListOf()
    }

    private fun isBetweenZeroAndOne(value: Float?): Boolean {
        return value != null && value in 0.0f..1.0f
    }
}