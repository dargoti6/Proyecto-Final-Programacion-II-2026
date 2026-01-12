public class ModuloHash {

    private int valorSemilla;

    public ModuloHash() {
        this.valorSemilla = 7; // valor fijo para el algoritmo
    }

    public String generarHash(String contrasenia) {
        if (contrasenia == null) {
            contrasenia = "";
        }

        int acumulador = valorSemilla;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < contrasenia.length(); i++) {
            char c = contrasenia.charAt(i);
            acumulador = acumulador * 31 + c + i;
            sb.append(Integer.toHexString(acumulador));
        }

        return sb.toString();
    }

    public boolean verificarContrasenia(String contrasenia, String hashAlmacenado) {
        if (hashAlmacenado == null) {
            return false;
        }
        String nuevoHash = generarHash(contrasenia);
        return hashAlmacenado.equals(nuevoHash);
    }
}
