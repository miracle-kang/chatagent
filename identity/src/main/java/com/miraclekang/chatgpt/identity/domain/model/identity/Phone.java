package com.miraclekang.chatgpt.identity.domain.model.identity;

import com.miraclekang.chatgpt.common.model.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Embeddable
public class Phone extends ValueObject {

    private static final Pattern CN_PHONE_PATTERN = Pattern.compile("^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$");
    private static final Pattern HK_PHONE_PATTERN = Pattern.compile("^([5689])\\d{7}$");
    private static final Pattern US_PHONE_PATTERN = Pattern.compile("^(1?|(1-)?)\\d{10,12}$");

    private static final String CHINA_AREA = "86";
    private static final String HK_AREA = "852";

    // @Comment("Phone area")
    @Column(name = "phone_area", length = 5)
    private String area;

    // @Comment("Phone number")
    @Column(name = "phone_number", length = 15)
    private String number;

    protected Phone() {
    }

    public Phone(String number) {
        this(guessArea(number), number);
    }

    public Phone(String area, String number) {
        Validate.notBlank(area, "Phone area must not be null.");
        Validate.notBlank(number, "Phone number must not be null.");
        Validate.isTrue(isValidPhoneNumber(number), "Phone number is invalid.");

        if (area.startsWith("+")) {
            area = area.substring(1);
        }
        this.area = area;
        this.number = number;
    }

    /**
     * 获取受保护的手机号码
     * CN: 15217808825 -> 152****8825
     * HK: 22224444    -> 22****44
     * US: 2223334444  -> 222****444
     *
     * @return 受保护的手机号码
     */
    public String protectedNumber() {
        if (StringUtils.isEmpty(number)) {
            return number;
        }
        if (number.length() == 8) {
            return StringUtils.overlay(number, "****", 2, 6);
        } else if (number.length() > 8) {
            return StringUtils.overlay(number, "****", 3, 7);
        }
        return number;
    }

    public String fullProtectedNumber() {
        if (area == null) {
            return protectedNumber();
        }
        return (area.startsWith("+") ? "" : "+") + area + " " + protectedNumber();
    }

    public String tail(int num) {
        if (StringUtils.isEmpty(number)) {
            return number;
        }
        if (number.length() <= num) {
            throw new IllegalArgumentException("Tail number must be less than phone number length.");
        }
        return number.substring(number.length() - num);
    }

    public Locale guessLocale() {
        if (area == null || area.equals("+86") || area.equals("86")) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        return Locale.TRADITIONAL_CHINESE;
    }

    /**
     * 猜测手机区号
     * <p>
     * 匹配是否为香港手机号，如果不是就返回默认手机区号
     *
     * @param number 手机号码
     * @return 区号
     */
    private static String guessArea(String number) {
        Validate.notBlank(number, "Phone number must be provided.");

        if (HK_PHONE_PATTERN.matcher(number).matches()) {
            return HK_AREA;
        }
        return CHINA_AREA;
    }

    /**
     * 判断手机号码是否合法
     *
     * @param number 手机号码
     */
    public static boolean isValidPhoneNumber(String number) {
        if (StringUtils.isBlank(number)) {
            return false;
        }
        return CN_PHONE_PATTERN.matcher(number).matches()
                || HK_PHONE_PATTERN.matcher(number).matches()
                || US_PHONE_PATTERN.matcher(number).matches();
    }


    public static Phone of(String number) {
        if (StringUtils.isBlank(number)) {
            return null;
        }
        return new Phone(number);
    }

    public static Phone of(String area, String number) {
        if (StringUtils.isBlank(number)) {
            return null;
        }
        return new Phone(area, number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone that = (Phone) o;
        return Objects.equals(area, that.area) && Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(area, number);
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "area='" + area + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
