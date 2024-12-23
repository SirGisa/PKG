# **Пользовательская документация**
## 1. Введение
Программа “Алгоритмы отсечения отрезков и многоугольников” предназначена для визуализации работы алгоритмов отсечения прямоугольным окном и выпуклыми многоугольниками. Приложение позволяет пользователям вводить данные о отрезках и многоугольниках, а затем наблюдать, как они обрезаются с использованием различных алгоритмов. Программа предоставляет возможность масштабирования, а также выводит результаты отсечения в графическом виде.

## 2. Системные требования
- Операционная система: Windows, macOS, Linux
- Программное обеспечение:
  - Python 3.x
  - Библиотеки: matplotlib, shapely
  - Аппаратные требования:
    - Минимум 1 ГБ ОЗУ
    - 100 МБ свободного места на диске

## 3. Установка

- 3.1 Установка Python

Перейдите на официальный сайт Python: python.org.
Скачайте последнюю версию Python 3.x.
Запустите установщик и следуйте инструкциям.

- 3.2 Установка зависимостей

После установки Python откройте командную строку.
Установите необходимые библиотеки с помощью команды:
``bash
pip install matplotlib shapely
``
- 3.3 Запуск программы
Скачайте файл algorithm_clipping.py или клонируйте репозиторий:
``bash
git clone [https://github.com/](https://github.com/SirGisa/PKG/otsech)
``
Перейдите в директорию:
``bash cd путь/к/папке/algorithm_clipping``
Запустите программу:
``bash python algorithm_clipping.py``

## 4. Использование приложения

4.1 Ввод данных
Для использования программы нужно ввести координаты отрезков и прямоугольного окна в текстовый файл формата .txt. Пример формата файла:

- 4
- 1 1 5 5
- 2 3 6 7
- -1 -1 0 0
- 4 4 7 7
- 1 1 4 4


4.2 Выбор алгоритма отсечения

Программа поддерживает два алгоритма отсечения:


Алгоритм Сазерленда-Коэна для отсечения отрезков.
Алгоритм отсечения выпуклого многоугольника.

4.3 Визуализация
Программа рисует прямоугольное окно отсечения (красным цветом).
Исходные отрезки (синим цветом) и видимые после отсечения части (зелёным цветом) отображаются на графике.

4.4 Масштабирование
Для изменения масштаба графика используйте соответствующие кнопки управления.

4.5 Очистка холста
Для удаления всех объектов на графике нажмите "Очистить".

## 5. Пример использования

- Пример 1. Отсечение отрезков

Входные данные:

- 3
- 2 3 5 5
- 0 0 6 6
- 1 1 4 4
- 2 2 4 4

### Алгоритм: Сазерленда-Коэна
Ожидаемый результат: Отсечённый отрезок с координатами (2, 2) и (4, 4).

- Пример 2. Отсечение многоугольника

  - Входные данные: многоугольник с координатами точек.

  - Алгоритм: отсечение выпуклого многоугольника

  - Ожидаемый результат: отсечённый многоугольник, оставшийся внутри заданного прямоугольного окна.

## 6. Обратная связь

Для вопросов и багов: bgmsch40@gmail.com
