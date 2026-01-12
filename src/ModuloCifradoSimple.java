public class ModuloCifradoSimple {

    private String abecedario;

    public ModuloCifradoSimple() {
        this.abecedario = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    }

    public String cifrarCesar(String texto, int desplazamiento) {
        if (texto == null) {
            return "";
        }
        StringBuilder resultado = new StringBuilder();
        int n = abecedario.length();
        desplazamiento = ((desplazamiento % n) + n) % n;

        String textoMayus = texto.toUpperCase();

        for (int i = 0; i < textoMayus.length(); i++) {
            char c = textoMayus.charAt(i);
            int pos = abecedario.indexOf(c);
            if (pos == -1) {
                resultado.append(c); // no es letra del abecedario
            } else {
                int nuevaPos = (pos + desplazamiento) % n;
                resultado.append(abecedario.charAt(nuevaPos));
            }
        }
        return resultado.toString();
    }

    public String descifrarCesar(String textoCifrado, int desplazamiento) {
        // descifrar es cifrar con el desplazamiento contrario
        return cifrarCesar(textoCifrado, -desplazamiento);
    }

    public String cifrarVigenere(String texto, String clave) {
        if (texto == null) {
            return "";
        }
        if (clave == null || clave.isEmpty()) {
            return texto; // sin clave no se puede cifrar, devolvemos igual
        }

        StringBuilder resultado = new StringBuilder();
        String textoMayus = texto.toUpperCase();
        String claveMayus = clave.toUpperCase();

        int n = abecedario.length();
        int indiceClave = 0;

        for (int i = 0; i < textoMayus.length(); i++) {
            char c = textoMayus.charAt(i);
            int posTexto = abecedario.indexOf(c);
            if (posTexto == -1) {
                resultado.append(c);
            } else {
                char cClave = claveMayus.charAt(indiceClave);
                int posClave = abecedario.indexOf(cClave);
                if (posClave == -1) {
                    posClave = 0;
                }
                int nuevaPos = (posTexto + posClave) % n;
                resultado.append(abecedario.charAt(nuevaPos));
                indiceClave = (indiceClave + 1) % claveMayus.length();
            }
        }
        return resultado.toString();
    }

    public String descifrarVigenere(String textoCifrado, String clave) {
        if (textoCifrado == null) {
            return "";
        }
        if (clave == null || clave.isEmpty()) {
            return textoCifrado;
        }

        StringBuilder resultado = new StringBuilder();
        String textoMayus = textoCifrado.toUpperCase();
        String claveMayus = clave.toUpperCase();

        int n = abecedario.length();
        int indiceClave = 0;

        for (int i = 0; i < textoMayus.length(); i++) {
            char c = textoMayus.charAt(i);
            int posTexto = abecedario.indexOf(c);
            if (posTexto == -1) {
                resultado.append(c);
            } else {
                char cClave = claveMayus.charAt(indiceClave);
                int posClave = abecedario.indexOf(cClave);
                if (posClave == -1) {
                    posClave = 0;
                }
                int nuevaPos = (posTexto - posClave);
                if (nuevaPos < 0) {
                    nuevaPos += n;
                }
                resultado.append(abecedario.charAt(nuevaPos));
                indiceClave = (indiceClave + 1) % claveMayus.length();
            }
        }
        return resultado.toString();
    }
}
