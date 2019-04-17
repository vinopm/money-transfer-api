package com.revolut.account;

public class Money implements Comparable<Money>{

    private final long pennies;

    Money(String pounds){
        this.pennies = parseToPennies(pounds);
    }

    Money(){
        this.pennies = 0L;
    }

    private Money(long pennies){
        this.pennies = pennies;
    }

    public static Money parseMoney(String s){
        return new Money(s);
    }

    private long parseToPennies(String s){
        if(s == null || s.isEmpty())
            throw new NumberFormatException("Input is empty.");

        var poundsAndPennies = s.split("\\.");

        if(poundsAndPennies.length == 1){
            //interpret input as pounds
            return Long.parseLong(s) * 100L;

        }else if(poundsAndPennies.length == 2){
            //interpret input as pounds and pence
            if(poundsAndPennies[0].isEmpty() || poundsAndPennies[1].length() != 2)
                throw new NumberFormatException("Two digits expected next to '.': " + s);

            var sign = sign(s);

            return sign * (Math.abs(Long.parseLong(poundsAndPennies[0]) * 100L) + Long.parseLong(poundsAndPennies[1]));

        }else{
            throw new NumberFormatException("Input is not in expected format: " + s);
        }
    }

    private int sign(String s){
        var num = Double.parseDouble(s);

        return num >= 0 ? 1 : -1;
    }

    private String sign(double d){
        return d >= 0 ? "" : "-";
    }

    @Override
    public String toString(){
        var strRepresentation = Long.toString(pennies);

        if(strRepresentation.length() > 2){
            var pounds = strRepresentation.substring(0, strRepresentation.length() - 2);
            var pence = strRepresentation.substring(strRepresentation.length() - 2);
            return pounds + "." + pence;
        }else if (strRepresentation.length() == 2){
            return sign(pennies) + "0." + pennies;
        }else if(strRepresentation.length() == 1){
            return sign(pennies) + "0.0" + pennies;
        }else {
            throw new NumberFormatException("Unable to convert: " + pennies + " into string representation.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        var money = (Money) o;
        return pennies == money.pennies;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(pennies);
    }

    Money add(Money money){
        var added = this.pennies + money.pennies;
        return new Money(added);
    }

    Money minus(Money money){
        var substracted = this.pennies - money.pennies;
        return new Money(substracted);
    }

    @Override
    public int compareTo(Money that) {
        return Long.compare(this.pennies, that.pennies);
    }
}
