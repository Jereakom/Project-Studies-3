package group1.oamk.ringo;


public class Contact {
    private String phone_number;
    private String pattern;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public long[] getPattern() {
        String[] pattern = this.pattern.split(",");
        long[] longPattern = new long[pattern.length];
        for (int i = 0; i < pattern.length ; i++) {
            longPattern[i] = Long.parseLong(pattern[i]);
        }
        return longPattern;

    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return pattern;
    }
}