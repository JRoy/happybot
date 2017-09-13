package com.wheezygold.happybot.embed;

/**
 * Created by Mike M on 6/28/2017.
 */
@SuppressWarnings("unused")
public enum Emoticon {

    /*
     * Alphabet
     */
    A(":regional_indicator_a: "),
    B(":regional_indicator_b: "),
    C(":regional_indicator_c: "),
    D(":regional_indicator_d: "),
    E(":regional_indicator_e: "),
    F(":regional_indicator_f: "),
    G(":regional_indicator_g: "),
    H(":regional_indicator_h: "),
    I(":regional_indicator_i: "),
    J(":regional_indicator_j: "),
    K(":regional_indicator_k: "),
    L(":regional_indicator_l: "),
    M(":regional_indicator_m: "),
    N(":regional_indicator_n: "),
    O(":regional_indicator_o: "),
    P(":regional_indicator_p: "),
    Q(":regional_indicator_q: "),
    R(":regional_indicator_r: "),
    S(":regional_indicator_s: "),
    T(":regional_indicator_t: "),
    U(":regional_indicator_u: "),
    V(":regional_indicator_v: "),
    W(":regional_indicator_w: "),
    X(":regional_indicator_x: "),
    Y(":regional_indicator_y: "),
    Z(":regional_indicator_z: "),

    /*
     * Numeric (0-9)
     */
    ZERO(":zero: "),
    ONE(":one: "),
    TWO(":two: "),
    THREE(":three: "),
    FOUR(":four: "),
    FIVE(":five: "),
    SIX(":six: "),
    SEVEN(":seven: "),
    EIGHT(":eight: "),
    NINE(":nine: "),
    ONE_TWO_THREE_FOUR(":1234: "),

    /*
     * Status Symbols
     */
    SUCCESS(":white_check_mark: "),
    ERROR(":x:"),
    ERROR_2(":no_entry_sign: "),

    /*
     * Rank Symbols
     */
    OWNER(":o2: "),
    CHQ_LEADER(":cl: "),
    BOT_DEV(":eight_spoked_asterisk:  "),
    ADMIN(":a: "),
    STAFF(":customs: "),
    PRIVILEGED(P.getTranslation()),
    MEMBER(M.getTranslation()),
    TROPHY(":trophy: "),

    /*
     * Faces
     */
    HAPPY(":grinning: "),
    UPSIDE_DOWN(":upside_down: "),
    SAD(":frowning2: "),

    /*
     * Statistic Symbols
     */
    GRAPH(":bar_chart: "),
    CHART_DOWN(":chart_with_downwards_trend: "),
    CHART_UP(":chart_with_upwards_trend: "),

    /*
     * Books
     */
    R_BOOK(":closed_book: "),
    O_BOOK(":orange_book: "),
    G_BOOK(":green_book: "),
    B_BOOK(":blue_book: "),
    IRL_BOOK(":notebook_with_decorative_cover: "),
    PENDING_MAIL(":incoming_envelope: "),

    /*
     * Clocks
     */
    CLOCK_1(":clock1: "),
    CLOCK_2(":clock2: "),
    CLOCK_3(":clock3: "),
    CLOCK_4(":clock4: "),
    CLOCK_5(":clock5: "),
    CLOCK_6(":clock6: "),
    CLOCK_7(":clock7: "),
    CLOCK_8(":clock8: "),
    CLOCK_9(":clock9: "),
    CLOCK_10(":clock10: "),
    CLOCK_11(":clock11: "),
    CLOCK_12(":clock12: "),
    STOPWATCH(":stopwatch: "),

    /*
     * Development Symbols
     */
    GEAR(":gear: "),
    WRENCH(":wrench: "),
    CLOUD(":cloud: "),
    DRIVE(":floppy_disk: "),
    RECEIVER(":pager: "),
    SLIDER(":level_slider: "),
    ID(":id: "),
    GAME(":video_game: ");

    private String translation;

    private Emoticon(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public static String fromString(String input) {
        return ":" + input + ": ";
    }

}
