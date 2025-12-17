package models.enumerations;

public enum MovieCertificate {
    U(3),
    UA(7),
    A(18),
    S(21);

    private final int minAge;

    MovieCertificate(int minAge) {
        this.minAge = minAge;
    }

    public int getMinAge() {
        return minAge;
    }
}
