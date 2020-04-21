package io.hashimati.microstarter.entity.micronaut.features;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

public class Java
{
    private String min;
    private String max;

    public Java(String min) {
        this.min = min;
    }

    public Java() {

    }

    public String getMin() {
        return min;
    }

    @Override
    public String toString() {
        return "Java{" +
                "min='" + min + '\'' +
                '}';
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }
}
