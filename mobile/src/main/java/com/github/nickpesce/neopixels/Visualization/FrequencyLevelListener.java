package com.github.nickpesce.neopixels.Visualization;

/**v
 * Created by nick on 8/28/16.
 */
public interface FrequencyLevelListener {
    void onFrequencyLevelsUpdate(double[] levels, double[] prevLevels, double[] variances);
}
