package io.hashimati.microstarter.entity.micronaut.features;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

public class JVMArgs {
    private String javaagent;


    public String getJavaagent() {
        return javaagent;
    }

    public void setJavaagent(String javaagent) {
        this.javaagent = javaagent;
    }

    @Override
    public String toString() {
        return "JVMArgs{" +
                "javaagent='" + javaagent + '\'' +
                '}';
    }
}
