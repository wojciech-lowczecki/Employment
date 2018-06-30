package pl.plh.app.employment.domain;

import java.util.Objects;

public final class PositiveLongInQueryDto {
    private final Long value;

    public PositiveLongInQueryDto(String text) {
        this(new Long(text));
    }

    public PositiveLongInQueryDto(final Long value) {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("value must be positive");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositiveLongInQueryDto)) return false;
        PositiveLongInQueryDto that = (PositiveLongInQueryDto) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
