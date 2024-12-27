import matplotlib.pyplot as plt
from tkinter import Tk, filedialog

# Константы для битов для Сазерленда-Коэна
LEFT = 1  # 0001
RIGHT = 2  # 0010
BOTTOM = 4  # 0100
TOP = 8  # 1000

# Функция для вычисления кода для точки
def compute_code(x, y, xmin, ymin, xmax, ymax):
    code = 0
    if x < xmin:  # точка слева от окна
        code |= LEFT
    elif x > xmax:  # точка справа от окна
        code |= RIGHT
    if y < ymin:  # точка ниже окна
        code |= BOTTOM
    elif y > ymax:  # точка выше окна
        code |= TOP
    return code

# Функция для отсечения отрезка с использованием алгоритма Сазерленда-Коэна
def sutherland_cohen_clip(segment, xmin, ymin, xmax, ymax):
    x1, y1, x2, y2 = segment
    code1 = compute_code(x1, y1, xmin, ymin, xmax, ymax)
    code2 = compute_code(x2, y2, xmin, ymin, xmax, ymax)
    accept = False

    while True:
        if code1 == 0 and code2 == 0:  # Оба конца отрезка внутри окна
            accept = True
            break
        elif (code1 & code2) != 0:  # Оба конца отрезка вне окна
            break
        else:
            # Один из концов отрезка вне окна
            code_out = code1 if code1 != 0 else code2
            if code_out & LEFT:
                x = xmin
                y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
            elif code_out & RIGHT:
                x = xmax
                y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
            elif code_out & BOTTOM:
                y = ymin
                x = x1 + (y - y1) * (x2 - x1) / (y2 - y1)
            elif code_out & TOP:
                y = ymax
                x = x1 + (y - y1) * (x2 - x1) / (y2 - y1)

            if code_out == code1:
                x1, y1 = x, y
                code1 = compute_code(x1, y1, xmin, ymin, xmax, ymax)
            else:
                x2, y2 = x, y
                code2 = compute_code(x2, y2, xmin, ymin, xmax, ymax)

    if accept:
        return (x1, y1, x2, y2)
    else:
        return None  # Отрезок полностью отсечен

# Визуализация данных
def plot_segments_and_window(segments, xmin, ymin, xmax, ymax, clipped_segments=None):
    fig, ax = plt.subplots()
    
    # Нарисуем отсекатель (прямоугольник)
    window = plt.Rectangle((xmin, ymin), xmax - xmin, ymax - ymin, linewidth=1, edgecolor='red', facecolor='none')
    ax.add_patch(window)

    # Нарисуем исходные отрезки
    for seg in segments:
        x1, y1, x2, y2 = seg
        ax.plot([x1, x2], [y1, y2], 'b-', lw=2)

    # Если есть отсечённые отрезки, нарисуем их
    if clipped_segments:
        for seg in clipped_segments:
            x1, y1, x2, y2 = seg
            ax.plot([x1, x2], [y1, y2], 'g-', lw=2)

    # Установим пределы осей
    ax.set_xlim(xmin - 2, xmax + 2)
    ax.set_ylim(ymin - 2, ymax + 2)
    ax.set_aspect('equal', adjustable='box')
    ax.grid(True)
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.show()

# Чтение данных из выбранного файла
def read_input_file(file_path):
    with open(file_path, 'r') as f:
        n = int(f.readline())  # количество отрезков
        segments = [tuple(map(int, f.readline().split())) for _ in range(n)]
        xmin, ymin, xmax, ymax = map(int, f.readline().split())
    return segments, xmin, ymin, xmax, ymax

# Функция для выбора файла
def select_file():
    root = Tk()
    root.withdraw()  # Прячем главное окно
    file_path = filedialog.askopenfilename(title="Выберите файл с данными", filetypes=[("Text files", "*.txt")])
    return file_path

# Пример использования
def main():
    while True:
        # Запрос на выбор файла
        file_path = select_file()
        if not file_path:  # Проверка, если файл не выбран
            print("Файл не выбран. Программа завершена.")
            break  # Выход из цикла, завершение программы

        # Чтение данных из выбранного файла
        segments, xmin, ymin, xmax, ymax = read_input_file(file_path)
        
        # Отсечение отрезков с использованием алгоритма Сазерленда-Коэна
        clipped_segments = []
        for seg in segments:
            clipped = sutherland_cohen_clip(seg, xmin, ymin, xmax, ymax)
            if clipped:
                clipped_segments.append(clipped)
        
        # Визуализация
        plot_segments_and_window(segments, xmin, ymin, xmax, ymax, clipped_segments)

        # Запрос на продолжение выбора файла
        cont = input("Хотите выбрать новый файл? (y/n): ").strip().lower()
        if cont != 'y':
            print("Программа завершена.")
            break

if __name__ == "__main__":
    main()
