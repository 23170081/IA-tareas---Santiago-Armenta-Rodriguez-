import tkinter as tk
import numpy as np
from PIL import Image, ImageDraw
import tensorflow as tf

# Cargamos el modelo entrenado con MNIST
model = tf.keras.models.load_model("mlp_model.keras")

# Tamaño de la ventana de dibujo (en píxeles)
CANVAS_SIZE = 280


class AppDibujo:
    def __init__(self, root):
        self.root = root
        self.root.title("Reconocimiento de Números (0 - 9)")

        # Lienzo visible en negro (igual que el fondo de MNIST)
        self.canvas = tk.Canvas(root, width=CANVAS_SIZE, height=CANVAS_SIZE, bg="black", cursor="cross")
        self.canvas.pack(padx=10, pady=10)

        # Imagen en memoria (280x280, escala de grises) para convertir fácil a 28x28
        self.image = Image.new("L", (CANVAS_SIZE, CANVAS_SIZE), color=0)
        self.draw = ImageDraw.Draw(self.image)

        # Eventos del ratón para dibujar presionando clic izquierdo
        self.canvas.bind("<ButtonPress-1>", self.iniciar_trazo)
        self.canvas.bind("<B1-Motion>", self.dibujar)
        self.canvas.bind("<ButtonRelease-1>", self.terminar_trazo)

        # Etiqueta con la predicción en tiempo real
        self.lbl_prediccion = tk.Label(root, text="Dibuja un número (0-9)", font=("Arial", 18, "bold"))
        self.lbl_prediccion.pack(pady=5)

        self.lbl_confianza = tk.Label(root, text="Confianza: -", font=("Arial", 11))
        self.lbl_confianza.pack(pady=2)

        # Botón para limpiar el lienzo
        btn_limpiar = tk.Button(root, text="Limpiar", font=("Arial", 12), command=self.limpiar)
        btn_limpiar.pack(pady=8)

        self.ultimo_x = None
        self.ultimo_y = None

    def iniciar_trazo(self, event):
        self.ultimo_x = event.x
        self.ultimo_y = event.y

    def dibujar(self, event):
        if self.ultimo_x is not None and self.ultimo_y is not None:
            radio = 11  # Grosor del trazo
            # Dibujamos en el canvas visual
            self.canvas.create_oval(
                event.x - radio, event.y - radio,
                event.x + radio, event.y + radio,
                fill="white", outline="white"
            )
            # Dibujamos en la imagen PIL en memoria
            self.draw.ellipse(
                [event.x - radio, event.y - radio, event.x + radio, event.y + radio],
                fill=255, outline=255
            )
            # Actualizamos la predicción en tiempo real mientras dibujas
            self.predecir()

        self.ultimo_x = event.x
        self.ultimo_y = event.y

    def terminar_trazo(self, event):
        self.ultimo_x = None
        self.ultimo_y = None
        self.predecir()

    def limpiar(self):
        self.canvas.delete("all")
        self.image = Image.new("L", (CANVAS_SIZE, CANVAS_SIZE), color=0)
        self.draw = ImageDraw.Draw(self.image)
        self.lbl_prediccion.config(text="Dibuja un número (0-9)")
        self.lbl_confianza.config(text="Confianza: -")

    def predecir(self):
        # Reducimos la imagen de 280x280 a 28x28 como espera el modelo
        img_28x28 = self.image.resize((28, 28), Image.Resampling.BILINEAR)

        # Convertimos a arreglo float32 y normalizamos entre 0 y 1 (igual que en el entrenamiento)
        arr = np.array(img_28x28, dtype="float32") / 255.0

        # Agregamos la dimensión de batch -> forma (1, 28, 28)
        entrada = np.expand_dims(arr, axis=0)

        # Obtenemos probabilidades para las 10 clases (0 al 9)
        probs = model.predict(entrada, verbose=0)[0]
        digito = int(np.argmax(probs))
        confianza = float(probs[digito]) * 100

        self.lbl_prediccion.config(text=f"Número detectado: {digito}")
        self.lbl_confianza.config(text=f"Confianza: {confianza:.1f}%")


if __name__ == "__main__":
    ventana = tk.Tk()
    app = AppDibujo(ventana)
    ventana.mainloop()