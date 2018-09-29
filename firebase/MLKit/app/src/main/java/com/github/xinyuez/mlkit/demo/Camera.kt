package com.github.xinyuez.mlkit.demo

import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.selector.LensPositionSelector
import io.fotoapparat.selector.back
import io.fotoapparat.selector.firstAvailable
import io.fotoapparat.selector.front
import io.fotoapparat.selector.highestResolution
import io.fotoapparat.selector.highestSensorSensitivity
import io.fotoapparat.selector.off
import io.fotoapparat.selector.standardRatio
import io.fotoapparat.selector.wideRatio

sealed class Camera(
    val lensPosition: LensPositionSelector,
    val configuration: CameraConfiguration
) {

    object Back : Camera(
        lensPosition = back(),
        configuration = CameraConfiguration(
            previewResolution = firstAvailable(
                wideRatio(highestResolution()),
                standardRatio(highestResolution())
            ),
            sensorSensitivity = highestSensorSensitivity()
        )
    )

    object Front : Camera(
        lensPosition = front(),
        configuration = CameraConfiguration(
            previewResolution = firstAvailable(
                wideRatio(highestResolution()),
                standardRatio(highestResolution())
            ),
            flashMode = off(),
            sensorSensitivity = highestSensorSensitivity()
        )
    )
}