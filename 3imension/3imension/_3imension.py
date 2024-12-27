import pygame
import numpy as np

# Constants
WIDTH, HEIGHT = 800, 600
BACKGROUND_COLOR = (255, 255, 255)
AXIS_COLOR_X = (255, 0, 0)  # Red for X-axis
AXIS_COLOR_Y = (0, 255, 0)  # Green for Y-axis
AXIS_COLOR_Z = (0, 0, 255)  # Blue for Z-axis
AXIS_LENGTH = 7  # Slightly longer for better visibility
FPS = 60
FONT_SIZE = 20


# Вершины буквы "Б" (в 3D) - проверено, корректные координаты
vertices = np.array([
    [0, 0, 0], [1, 0, 0], [1, 1, 0], [0.5, 1, 0], [0.5, 1.5, 0], [1, 1.5, 0], [1, 2, 0], [0, 2, 0], [0.3, 0.3, 0], [0.7, 0.3, 0], [0.7, 0.7, 0], [0.3, 0.7, 0],
    [0, 0, 1], [1, 0, 1], [1, 1, 1], [0.5, 1, 1], [0.5, 1.5, 1], [1, 1.5, 1], [1, 2, 1], [0, 2, 1], [0.3, 0.3, 1], [0.7, 0.3, 1], [0.7, 0.7, 1], [0.3, 0.7, 1]
])

# Ребра буквы "Б" -  Corrected: removed (12, 24)
edges = [   
    (0, 1), (1, 2), (2, 3), (3, 4), (4, 5), (5, 6), (6, 7), (7, 0), (8, 9), (9, 10), (10, 11), (11, 8),
    (12, 13), (13, 14), (14, 15), (15, 16), (16, 17), (17, 18), (18, 19), (19, 12), (20, 21), (21, 22), (22, 23), (23, 20),
    (0, 12), (1, 13), (2, 14), (3, 15), (4, 16), (5, 17), (6, 18), (7, 19), (8, 20), (9, 21), (10, 22), (11, 23) # Removed erroneous edge
]


def project(point, transformation_matrix):
    """Projects a 3D point to 2D using the provided transformation matrix."""
    transformed_point = point.dot(transformation_matrix)
    return (int(transformed_point[0] + WIDTH / 2), int(-transformed_point[1] + HEIGHT / 2))


def rotation_matrix(angle, axis):
    """Creates a rotation matrix for the given angle and axis."""
    angle = np.radians(angle)
    axis = axis / np.linalg.norm(axis)
    x, y, z = axis
    return np.array([
        [np.cos(angle) + x**2 * (1 - np.cos(angle)), x * y * (1 - np.cos(angle)) - z * np.sin(angle), x * z * (1 - np.cos(angle)) + y * np.sin(angle)],
        [y * x * (1 - np.cos(angle)) + z * np.sin(angle), np.cos(angle) + y**2 * (1 - np.cos(angle)), y * z * (1 - np.cos(angle)) - x * np.sin(angle)],
        [z * x * (1 - np.cos(angle)) - y * np.sin(angle), z * y * (1 - np.cos(angle)) + x * np.sin(angle), np.cos(angle) + z**2 * (1 - np.cos(angle))]
    ])




def main():
    pygame.init()
    screen = pygame.display.set_mode((WIDTH, HEIGHT))
    pygame.display.set_caption("3D Projection of 'Б'")
    clock = pygame.time.Clock()
    font = pygame.font.Font(None, FONT_SIZE) #  Initialize font

    angle_x = angle_y = angle_z = 0
    scale_factor = 40
    prev_mouse_pos = None

    # Вершины осей координат (не вращаются)
    axis_vertices = np.array([[0, 0, 0], [AXIS_LENGTH, 0, 0], [0, AXIS_LENGTH, 0], [0, 0, AXIS_LENGTH]])

    running = True
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.MOUSEBUTTONDOWN and event.button == 1:
                prev_mouse_pos = event.pos
            elif event.type == pygame.MOUSEMOTION and prev_mouse_pos and event.buttons[0]:
                dx, dy = event.pos[0] - prev_mouse_pos[0], event.pos[1] - prev_mouse_pos[1]
                angle_x += dy * 0.5
                angle_y += dx * 0.5
                angle_z += (dx + dy) * 0.25
                prev_mouse_pos = event.pos
            elif event.type == pygame.MOUSEBUTTONUP and event.button == 1:
                prev_mouse_pos = None  # Сброс позиции мыши после отпускания кнопки
            elif event.type == pygame.MOUSEWHEEL:
                scale_factor += event.y * 0.1
                scale_factor = max(0.1, scale_factor) #  Ограничение минимального масштаба

        screen.fill(BACKGROUND_COLOR)

        # Calculate transformation matrix (Corrected)
        rotation_x = rotation_matrix(angle_x, np.array([1, 0, 0]))
        rotation_y = rotation_matrix(angle_y, np.array([0, 1, 0]))
        rotation_z = rotation_matrix(angle_z, np.array([0, 0, 1]))
        scaling_matrix = np.diag([scale_factor, scale_factor, scale_factor])
        transformation_matrix = rotation_x.dot(rotation_y).dot(rotation_z).dot(scaling_matrix) # No slicing here!

        # Draw axes with labels
        for i, color, label in zip(range(1, 4), [AXIS_COLOR_X, AXIS_COLOR_Y, AXIS_COLOR_Z], ["X", "Y", "Z"]):
            transformed_axis_end = axis_vertices[i].dot(transformation_matrix) # Transform to 3D first
            axis_end = (int(transformed_axis_end[0] + WIDTH / 2), int(-transformed_axis_end[1] + HEIGHT / 2)) # Then project
            pygame.draw.line(screen, color, project(axis_vertices[0], transformation_matrix), axis_end, 2)
            label_surface = font.render(label, True, color)
            screen.blit(label_surface, (axis_end[0] + 5, axis_end[1] - 5))


        # Draw the letter "Б" (Corrected)
        for edge in edges:
            transformed_p1 = vertices[edge[0]].dot(transformation_matrix) # Transform to 3D first
            transformed_p2 = vertices[edge[1]].dot(transformation_matrix)
            p1 = (int(transformed_p1[0] + WIDTH / 2), int(-transformed_p1[1] + HEIGHT / 2)) # Then project
            p2 = (int(transformed_p2[0] + WIDTH / 2), int(-transformed_p2[1] + HEIGHT / 2))
            pygame.draw.line(screen, (0, 0, 0), p1, p2, 2)



        # Display transformation matrix
        matrix_string = str(transformation_matrix.round(2))
        matrix_surface = font.render(matrix_string, True, (0, 0, 0))
        screen.blit(matrix_surface, (10, 10))

        pygame.display.flip()
        clock.tick(FPS)

    pygame.quit()



if __name__ == "__main__":
    main()