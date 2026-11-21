class Perceptron:
    def __init__(self, num_entradas, tasa_aprendizaje=0.1, epocas=20):
        # Inicializamos pesos en 0 y el sesgo (bias) en 0
        self.pesos = [0.0] * num_entradas
        self.sesgo = 0.0
        self.tasa_aprendizaje = tasa_aprendizaje
        self.epocas = epocas

    def funcion_activacion(self, suma):
        # Función escalón: devuelve 1 si la suma es >= 0, de lo contrario 0
        return 1 if suma >= 0 else 0

    def predecir(self, entradas):
        # Calcula la combinación lineal: w1*x1 + w2*x2 + ... + bias
        suma = sum(w * x for w, x in zip(self.pesos, entradas)) + self.sesgo
        return self.funcion_activacion(suma)

    def entrenar(self, datos_entrenamiento, etiquetas):
        for epoca in range(self.epocas):
            errores = 0
            for entradas, etiqueta_real in zip(datos_entrenamiento, etiquetas):
                prediccion = self.predecir(entradas)
                error = etiqueta_real - prediccion

                if error != 0:
                    errores += 1
                    # Actualizamos pesos y sesgo usando la regla del perceptrón
                    for i in range(len(self.pesos)):
                        self.pesos[i] += self.tasa_aprendizaje * error * entradas[i]
                    self.sesgo += self.tasa_aprendizaje * error

            # Si no hubo errores en toda la época, ya convergió
            if errores == 0:
                print(f"Convergió en la época {epoca + 1}")
                break


# --- Ejemplo: Compuerta lógica AND ---
X = [
    [0, 0],
    [0, 1],
    [1, 0],
    [1, 1],
]
y = [0, 0, 0, 1]

perceptron = Perceptron(num_entradas=2, tasa_aprendizaje=0.1, epocas=20)
perceptron.entrenar(X, y)

print("\nPesos aprendidos:", perceptron.pesos)
print("Sesgo aprendido:", perceptron.sesgo)

print("\nPruebas:")
for entrada in X:
    print(f"Entrada: {entrada} -> Salida: {perceptron.predecir(entrada)}")