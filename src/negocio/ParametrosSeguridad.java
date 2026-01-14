package negocio;

public class ParametrosSeguridad {

    private int minPasswordLength = 8;
    private boolean requireUpper = true;
    private boolean requireLower = true;
    private boolean requireDigit = true;
    private int maxIntentosFallidosLogin = 5;

    public ParametrosSeguridad() {}

    public int getMinPasswordLength() { return minPasswordLength; }
    public void setMinPasswordLength(int v) { this.minPasswordLength = v; }

    public boolean isRequireUpper() { return requireUpper; }
    public void setRequireUpper(boolean requireUpper) { this.requireUpper = requireUpper; }

    public boolean isRequireLower() { return requireLower; }
    public void setRequireLower(boolean requireLower) { this.requireLower = requireLower; }

    public boolean isRequireDigit() { return requireDigit; }
    public void setRequireDigit(boolean requireDigit) { this.requireDigit = requireDigit; }

    public int getMaxIntentosFallidosLogin() { return maxIntentosFallidosLogin; }
    public void setMaxIntentosFallidosLogin(int v) { this.maxIntentosFallidosLogin = v; }

    @Override
    public String toString() {
        return "ParámetrosSeguridad{" +
                "minPasswordLength=" + minPasswordLength +
                ", requireUpper=" + requireUpper +
                ", requireLower=" + requireLower +
                ", requireDigit=" + requireDigit +
                ", maxIntentosFallidosLogin=" + maxIntentosFallidosLogin +
                '}';
    }
}
