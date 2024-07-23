package zeggiotti.guitartuner;

public enum Note {

    C2, CS2, D2, EB2, E2, F2, FS2, G2, GS2, H2, A2, BB2, B2,
    C3, CS3, D3, EB3, E3, F3, FS3, G3, GS3, H3, A3, BB3, B3,
    C4, CS4, D4, EB4, E4, F4, FS4, G4, GS4, H4, A4, BB4, B4;

    public String toString () {
        String result = super.toString();
        if(super.toString().length() > 2) {
            if(super.toString().charAt(1) == 'S') {
                result = result.charAt(0) + "#" + result.charAt(2);
            } else {
                result = result.charAt(0) + "b" + result.charAt(2);
            }
        }

        result = result.substring(0, result.length() - 1) + getSubscriptChar(result.charAt(result.length() - 1));
        return result;
    }

    private static String getSubscriptChar(char c) {
        return switch (c) {
            case '1' -> "\u2081";
            case '2' -> "\u2082";
            case '3' -> "\u2083";
            case '4' -> "\u2084";
            case '5' -> "\u2085";
            case '6' -> "\u2086";
            case '7' -> "\u2087";
            case '8' -> "\u2088";
            case '9' -> "\u2089";
            default -> String.valueOf(c);
        };
    }

}
