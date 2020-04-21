package io.hashimati.microstarter.entity;
/**
 * @author Ahmed Al Hashmi @hashimati
 */


import org.apache.commons.lang3.StringUtils;

import static io.hashimati.microstarter.constants.ProjectConstants.EntityAttributeType.*;
import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.*;


public class EntityAttribute {
    private String name, type, typePackage;
    private boolean premetive =true;

    private boolean array=false;

    private EntityConstraints constraints;

    public EntityAttribute()
    {}
    public boolean isString(){

        return this.type.toString().trim().equalsIgnoreCase(STRING);
    }
    public boolean isInteger(){
        return this.type.toString().trim().equalsIgnoreCase(INTEGER);

    }
    public boolean isDate(){
        return this.type.toString().trim().equalsIgnoreCase(DATE);

    }
    public boolean isDouble(){
        return this.type.toString().trim().equalsIgnoreCase(DOUBLE);

    }
    public boolean isFloat(){

        return this.type.toString().trim().equalsIgnoreCase(FLOAT);

    }
    public boolean isByte(){
        return this.type.toString().trim().equalsIgnoreCase(BYTE);

    }
    public boolean isShort(){
        return this.type.toString().trim().equalsIgnoreCase(SHORT);

    }
    public boolean isLong(){
        return this.type.toString().trim().equalsIgnoreCase(LONG);

    }
    public boolean isChar(){
        return this.type.toString().trim().equalsIgnoreCase(CHAR);

    }
    public boolean isBigInteger()
    {
        return this.type.toString().trim().equalsIgnoreCase(BIG_INTEGER);

    }
    public boolean isBoolean()
    {
        return this.type.toString().trim().equalsIgnoreCase(BOOLEAN);

    }
    public boolean isBigDouble()
    {
        return this.type.toString().trim().equalsIgnoreCase(BIG_DECIMAL);

    }
    public boolean isClass()
    {
        return !isString()
                || !isPermetiveDataType();
    }

    private boolean isPermetiveDataType() {
        return isByte() || isInteger() || isShort() || isChar() || isDouble() || isFloat();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypePackage() {
        return typePackage;
    }

    public void setTypePackage(String typePackage) {
        this.typePackage = typePackage;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType()
    {
        return this.type;
    }


    public String getDeclaration(String lang)
    {
        String constraintsDeclaration="";
//        if(constraints != null)
//        {
//            if(constraints.isEnabled())
//            {
//                if(isString())
//                {
//                    if(!constraints.isNullable())
//                    {
//                        constraintsDeclaration +="\t" +constraints.getNotNullExpression();
//
//                    }
//                    if(constraints.getMaxSize()!=null && constraints.getMinSize() !=null)
//                    {
//                        constraintsDeclaration +=  constraints.getSizeExpression();
//
//                    }
//                    if(constraints.getPattern() != null)
//                    {
//                        constraintsDeclaration +="\t" + constraints.getPatternExpression();
//
//                    }
//                    if(constraints.isEmail())
//                    {
//                        constraintsDeclaration += constraints.getEmailExpression();
//                    }
//                    if(constraints.isNotBlank())
//                    {
//                        constraintsDeclaration += constraints.getNotBlankExpression() ;
//
//                    }
//                }
//
//                else if(isInteger())
//                {
//                    if(constraints.getMin() != null)
//                    {
//                        constraintsDeclaration += constraints.getMinExpression();
//                    }
//                    if(constraints.getMax() != null) {
//                        constraintsDeclaration += constraints.getMaxExpression();
//                    }
//                }
//
//            }
//
//        }
        switch (lang)
        {
            case JAVA_LANG:

                return "\tprivate " +type + " " + name +";\n";


            case GROOVY_LANG:
                return "\t" +type + " " + name +";\n";
            case KOTLIN_LANG:
                return "\t" +"var" + " " + name + ":" + StringUtils.capitalize(type) +"\n";
        }
        return "";
    }
    public String getPackageSyntax(String lang)
    {
        if(this.getType().equalsIgnoreCase(BIG_DECIMAL))
        {
            return "import " + BIG_DECIMAL_CLASS + ";";
        }
        if(this.getType().equalsIgnoreCase(BIG_DECIMAL))
        {
            return "import " +BIG_INTEGER_CLASS + ";";
        }
        switch (lang)
        {
            case JAVA_LANG:
            case GROOVY_LANG:
            case KOTLIN_LANG:
                return "import " + getTypePackage() + ";";
        }
        return "";
    }

    public boolean isPremetive() {
        return premetive;
    }

    public void setPremetive(boolean premetive) {
        this.premetive = premetive;
    }

    public EntityConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(EntityConstraints constraints) {
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        return "EntityAttribute{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", typePackage='" + typePackage + '\'' +
                ", premetive=" + premetive +
                ", array=" + array +
                ", constraints=" + constraints +
                '}';
    }

    public boolean isArray() {
        return array;
    }

    public void setArray(boolean array) {
        this.array = array;
    }
}
