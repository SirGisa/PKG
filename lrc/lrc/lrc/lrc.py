import tkinter as tk
from tkinter import messagebox, colorchooser

def rgb_to_lab(rgb):
    r, g, b = [x / 255.0 for x in rgb]
    r = r ** 2.2
    g = g ** 2.2
    b = b ** 2.2

    x = r * 0.4124564 + g * 0.3575761 + b * 0.1804375
    y = r * 0.2126729 + g * 0.7151522 + b * 0.0721750
    z = r * 0.0193339 + g * 0.1191920 + b * 0.9503041

    x /= 0.95047
    y /= 1.00000
    z /= 1.08883

    l = max(0, min(100, 116 * y ** (1/3) - 16))
    a = max(-128, min(127, 500 * (x ** (1/3) - y ** (1/3))))
    b = max(-128, min(127, 200 * (y ** (1/3) - z ** (1/3))))

    return (l, a, b)

def rgb_to_cmyk(rgb):
    r, g, b = rgb
    c = 1 - (r / 255)
    m = 1 - (g / 255)
    y = 1 - (b / 255)
    k = min(c, m, y)

    if k < 1:
        c = (c - k) / (1 - k)
        m = (m - k) / (1 - k)
        y = (y - k) / (1 - k)
    else:
        c = m = y = 0

    return (c, m, y, k)

class ColorConverter:
    def __init__(self):
        self.updating = False  # Флаг для предотвращения зацикливаний

    def update_color(self):
        if self.updating:
            return
        self.updating = True

        r = red_slider.get()
        g = green_slider.get()
        b = blue_slider.get()
        rgb = (r, g, b)

        lab = rgb_to_lab(rgb)
        cmyk = rgb_to_cmyk(rgb)

        rgb_label.config(text=f"RGB: {rgb}")
        lab_label.config(text=f"LAB: {lab}")
        cmyk_label.config(text=f"CMYK: {cmyk}")

        color_display.config(bg=f'#{r:02x}{g:02x}{b:02x}')

        rgb_r_entry.delete(0, tk.END)
        rgb_r_entry.insert(0, str(r))
        rgb_g_entry.delete(0, tk.END)
        rgb_g_entry.insert(0, str(g))
        rgb_b_entry.delete(0, tk.END)
        rgb_b_entry.insert(0, str(b))

        # Обновление слайдеров LAB и CMYK
        lab_L_slider.set(lab[0])
        lab_a_slider.set(lab[1])
        lab_b_slider.set(lab[2])

        cmyk_c_slider.set(cmyk[0])
        cmyk_m_slider.set(cmyk[1])
        cmyk_y_slider.set(cmyk[2])
        cmyk_k_slider.set(cmyk[3])

        self.updating = False

    def update_rgb_and_color(self, value):
        self.update_color()

    def choose_color(self):
        color = colorchooser.askcolor(title="Выбрать цвет")
        if color[0]:  # Если выбран цвет
            r, g, b = map(int, color[0])
            red_slider.set(r)
            green_slider.set(g)
            blue_slider.set(b)
            self.update_color()

    def update_from_lab(self):
        if self.updating:
            return
        self.updating = True

        L = lab_L_slider.get()
        a = lab_a_slider.get()
        b = lab_b_slider.get()

        # Примерное преобразование LAB в RGB (обратное)
        r = int((L + a) * 2)
        g = int((L - b) * 2)
        b = int(L * 2)

        red_slider.set(max(0, min(255, r)))
        green_slider.set(max(0, min(255, g)))
        blue_slider.set(max(0, min(255, b)))
        
        # Обновляем цвет панели, даже если RGB-ползунки на 0
        self.update_color()

        self.updating = False

    def update_from_cmyk(self):
        if self.updating:
            return
        self.updating = True

        c = cmyk_c_slider.get()
        m = cmyk_m_slider.get()
        y = cmyk_y_slider.get()
        k = cmyk_k_slider.get()

        r = int(255 * (1 - c) * (1 - k))
        g = int(255 * (1 - m) * (1 - k))
        b = int(255 * (1 - y) * (1 - k))

        red_slider.set(max(0, min(255, r)))
        green_slider.set(max(0, min(255, g)))
        blue_slider.set(max(0, min(255, b)))
        
        # Обновляем цвет панели, даже если RGB-ползунки на 0
        self.update_color()

        self.updating = False

    def update_rgb_from_entry(self):
        try:
            r = int(rgb_r_entry.get())
            g = int(rgb_g_entry.get())
            b = int(rgb_b_entry.get())
            if 0 <= r <= 255 and 0 <= g <= 255 and 0 <= b <= 255:
                red_slider.set(r)
                green_slider.set(g)
                blue_slider.set(b)
                self.update_color()
            else:
                messagebox.showerror("Ошибка", "Значения RGB должны быть в диапазоне от 0 до 255.")
        except ValueError:
            messagebox.showerror("Ошибка", "Пожалуйста, введите корректные числовые значения.")

    def update_lab_from_entry(self):
        try:
            L = float(lab_L_entry.get())
            a = float(lab_a_entry.get())
            b = float(lab_b_entry.get())
            if 0 <= L <= 100 and -128 <= a <= 127 and -128 <= b <= 127:
                lab_L_slider.set(L)
                lab_a_slider.set(a)
                lab_b_slider.set(b)
                self.update_from_lab()
            else:
                messagebox.showerror("Ошибка", "Пожалуйста, введите корректные значения для LAB.")
        except ValueError:
            messagebox.showerror("Ошибка", "Пожалуйста, введите корректные числовые значения.")

    def update_cmyk_from_entry(self):
        try:
            c = float(cmyk_c_entry.get())
            m = float(cmyk_m_entry.get())
            y = float(cmyk_y_entry.get())
            k = float(cmyk_k_entry.get())
            if 0 <= c <= 1 and 0 <= m <= 1 and 0 <= y <= 1 and 0 <= k <= 1:
                cmyk_c_slider.set(c)
                cmyk_m_slider.set(m)
                cmyk_y_slider.set(y)
                cmyk_k_slider.set(k)
                self.update_from_cmyk()
            else:
                messagebox.showerror("Ошибка", "Значения CMYK должны быть в диапазоне от 0 до 1.")
        except ValueError:
            messagebox.showerror("Ошибка", "Пожалуйста, введите корректные числовые значения.")

# Создание основного окна
root = tk.Tk()
root.title("Цветовые модели")

# Фреймы для организации виджетов
rgb_frame = tk.Frame(root)
lab_frame = tk.Frame(root)
cmyk_frame = tk.Frame(root)

rgb_frame.pack(side=tk.LEFT, padx=10)
lab_frame.pack(side=tk.LEFT, padx=10)
cmyk_frame.pack(side=tk.LEFT, padx=10)

# Инициализация экземпляра ColorConverter
color_converter = ColorConverter()

# Слайдеры и текстовые поля для RGB
tk.Label(rgb_frame, text="RGB").grid(row=0, columnspan=2)

tk.Label(rgb_frame, text="R:").grid(row=1, column=0)
rgb_r_entry = tk.Entry(rgb_frame)
rgb_r_entry.grid(row=1, column=1)

red_slider = tk.Scale(rgb_frame, from_=0, to=255, orient='horizontal', command=color_converter.update_rgb_and_color)
red_slider.grid(row=2, columnspan=2)

tk.Label(rgb_frame, text="G:").grid(row=3, column=0)
rgb_g_entry = tk.Entry(rgb_frame)
rgb_g_entry.grid(row=3, column=1)

green_slider = tk.Scale(rgb_frame, from_=0, to=255, orient='horizontal', command=color_converter.update_rgb_and_color)
green_slider.grid(row=4, columnspan=2)

tk.Label(rgb_frame, text="B:").grid(row=5, column=0)
rgb_b_entry = tk.Entry(rgb_frame)
rgb_b_entry.grid(row=5, column=1)

blue_slider = tk.Scale(rgb_frame, from_=0, to=255, orient='horizontal', command=color_converter.update_rgb_and_color)
blue_slider.grid(row=6, columnspan=2)

# Кнопка для обновления RGB
rgb_button = tk.Button(rgb_frame, text="Обновить RGB", command=color_converter.update_rgb_from_entry)
rgb_button.grid(row=7, columnspan=2)

# Слайдеры и текстовые поля для LAB
tk.Label(lab_frame, text="LAB").grid(row=0, columnspan=2)

tk.Label(lab_frame, text="L:").grid(row=1, column=0)
lab_L_entry = tk.Entry(lab_frame)
lab_L_entry.grid(row=1, column=1)

lab_L_slider = tk.Scale(lab_frame, from_=0, to=100, orient='horizontal', command=lambda x: color_converter.update_from_lab())
lab_L_slider.grid(row=2, columnspan=2)

tk.Label(lab_frame, text="a:").grid(row=3, column=0)
lab_a_entry = tk.Entry(lab_frame)
lab_a_entry.grid(row=3, column=1)

lab_a_slider = tk.Scale(lab_frame, from_=-128, to=127, orient='horizontal', command=lambda x: color_converter.update_from_lab())
lab_a_slider.grid(row=4, columnspan=2)

tk.Label(lab_frame, text="b:").grid(row=5, column=0)
lab_b_entry = tk.Entry(lab_frame)
lab_b_entry.grid(row=5, column=1)

lab_b_slider = tk.Scale(lab_frame, from_=-128, to=127, orient='horizontal', command=lambda x: color_converter.update_from_lab())
lab_b_slider.grid(row=6, columnspan=2)

lab_button = tk.Button(lab_frame, text="Обновить LAB", command=color_converter.update_lab_from_entry)
lab_button.grid(row=7, columnspan=2)

# Слайдеры и текстовые поля для CMYK
tk.Label(cmyk_frame, text="CMYK").grid(row=0, columnspan=2)

tk.Label(cmyk_frame, text="C:").grid(row=1, column=0)
cmyk_c_entry = tk.Entry(cmyk_frame)
cmyk_c_entry.grid(row=1, column=1)

cmyk_c_slider = tk.Scale(cmyk_frame, from_=0, to=1, resolution=0.01, orient='horizontal', command=lambda x: color_converter.update_from_cmyk())
cmyk_c_slider.grid(row=2, columnspan=2)

tk.Label(cmyk_frame, text="M:").grid(row=3, column=0)
cmyk_m_entry = tk.Entry(cmyk_frame)
cmyk_m_entry.grid(row=3, column=1)

cmyk_m_slider = tk.Scale(cmyk_frame, from_=0, to=1, resolution=0.01, orient='horizontal', command=lambda x: color_converter.update_from_cmyk())
cmyk_m_slider.grid(row=4, columnspan=2)

tk.Label(cmyk_frame, text="Y:").grid(row=5, column=0)
cmyk_y_entry = tk.Entry(cmyk_frame)
cmyk_y_entry.grid(row=5, column=1)

cmyk_y_slider = tk.Scale(cmyk_frame, from_=0, to=1, resolution=0.01, orient='horizontal', command=lambda x: color_converter.update_from_cmyk())
cmyk_y_slider.grid(row=6, columnspan=2)

tk.Label(cmyk_frame, text="K:").grid(row=7, column=0)
cmyk_k_entry = tk.Entry(cmyk_frame)
cmyk_k_entry.grid(row=7, column=1)

cmyk_k_slider = tk.Scale(cmyk_frame, from_=0, to=1, resolution=0.01, orient='horizontal', command=lambda x: color_converter.update_from_cmyk())
cmyk_k_slider.grid(row=8, columnspan=2)

cmyk_button = tk.Button(cmyk_frame, text="Обновить CMYK", command=color_converter.update_cmyk_from_entry)
cmyk_button.grid(row=9, columnspan=2)

# Кнопка для выбора цвета
choose_button = tk.Button(root, text="Выбрать цвет", command=color_converter.choose_color)
choose_button.pack(pady=10)

# Метки для отображения цветов
color_display = tk.Label(root, text=' ', width=40, height=10)
color_display.pack(pady=10)

rgb_label = tk.Label(root, text="RGB: (0, 0, 0)")
rgb_label.pack()

lab_label = tk.Label(root, text="LAB: (0, 0, 0)")
lab_label.pack()

cmyk_label = tk.Label(root, text="CMYK: (0, 0, 0, 0)")
cmyk_label.pack()

# Запуск основного цикла
color_converter.update_color()  # Инициализация с начальным цветом
root.mainloop()