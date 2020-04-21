package io.hashimati.microstarter.config;

import java.util.HashMap;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

//@NoArgsConstructor
//@Data
//@ToString
public  class MappedVariable {
        private String file,
                className;
        private HashMap<String, String> variabelMethod;

        public String getFile() {
                return file;
        }

        @Override
        public String toString() {
                return "MappedVariable{" +
                        "file='" + file + '\'' +
                        ", className='" + className + '\'' +
                        ", variabelMethod=" + variabelMethod +
                        '}';
        }

        public void setFile(String file) {
                this.file = file;
        }

        public String getClassName() {
                return className;
        }

        public void setClassName(String className) {
                this.className = className;
        }

        public HashMap<String, String> getVariabelMethod() {
                return variabelMethod;
        }

        public void setVariabelMethod(HashMap<String, String> variabelMethod) {
                this.variabelMethod = variabelMethod;
        }
}

