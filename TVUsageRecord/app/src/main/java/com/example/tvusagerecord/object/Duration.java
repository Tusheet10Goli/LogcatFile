package com.example.tvusagerecord.object;

/**
 * object class of Duration
 */
public class Duration {

    /** total hours watching TV */
    int total;
    /** hours watching TV morning */
    int morning;
    /** hours watching TV noon */
    int noon;
    /** hours watching TV afternoon */
    int afternoon;
    /** hours watching TV evening */
    int evening;
    /** hours watching TV at late night */
    int night;


    /**
     * constructor of Duration with no parameter, set everything to 0
     */
    public Duration () {
        setTotal(0);
        setMorning(0);
        setNoon(0);
        setAfternoon(0);
        setEvening(0);
        setNight(0);
    }


    /**
     * constructor of Duration with parameters
     * @param morning
     * @param noon
     * @param afternoon
     * @param evening
     * @param night
     */
    public Duration(int total, int morning, int noon, int afternoon, int evening, int night) {
        setTotal(total);
        setMorning(morning);
        setNoon(noon);
        setAfternoon(afternoon);
        setEvening(evening);
        setNight(night);
    }


    /**
     * Multiple setters
     */
    public void setTotal(int hours) {
        this.total = hours;
    }

    public void setMorning(int hours) {
        this.morning = hours;
    }

    public void setNoon(int hours) {
        this.noon = hours;
    }

    public void setAfternoon(int hours) {
        this.afternoon = hours;
    }

    public void setEvening(int hours) {
        this.evening = hours;
    }

    public void setNight(int hours) {
        this.night = hours;
    }


    /**
     * Multiple getters
     */
    public int getTotal() {
        return total;
    }

    public int getMorning() {
        return morning;
    }

    public int getNoon() {
        return noon;
    }

    public int getAfternoon() {
        return afternoon;
    }

    public int getEvening() {
        return evening;
    }

    public int getNight() {
        return night;
    }
}