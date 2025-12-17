// Показать окно входа
function showLogin() {
    $('#registerModal').modal('hide');
    $('#loginModal').modal('show');
}

// Показать окно регистрации
function showRegister() {
    $('#loginModal').modal('hide');
    $('#registerModal').modal('show');
}

// Проверить авторизацию при загрузке страницы
function checkAuth() {
    $.get('/auth/check', function(response) {
        if (response.startsWith('AUTHENTICATED')) {
            const parts = response.split(':');
            const role = parts[1];
            const name = parts[2];

            // Показываем информацию о пользователе
            $('#userName').text(name);
            $('#authButtons').addClass('d-none');
            $('#userInfo').removeClass('d-none');

            // Показываем ссылку на админку если админ
            if (role === 'ADMIN') {
                $('#adminLink').removeClass('d-none');
            }

            // Закрываем модальные окна если открыты
            $('#loginModal').modal('hide');
            $('#registerModal').modal('hide');
        }
    });
}

// Обработка формы входа
$('#loginForm').submit(function(e) {
    e.preventDefault();

    const formData = $(this).serialize();

    $.post('/auth/login', formData, function(response) {
        if (response.startsWith('SUCCESS')) {
            // Обновляем страницу
            location.reload();
        } else {
            // Показываем ошибку
            const error = response.replace('ERROR: ', '');
            $('#loginError').text(error).removeClass('d-none');
        }
    });
});

// Обработка формы регистрации
$('#registerForm').submit(function(e) {
    e.preventDefault();

    const formData = $(this).serialize();

    $.post('/auth/register', formData, function(response) {
        if (response.startsWith('SUCCESS')) {
            // Обновляем страницу
            location.reload();
        } else {
            // Показываем ошибку
            const error = response.replace('ERROR: ', '');
            $('#registerError').text(error).removeClass('d-none');
        }
    });
});

// Проверяем авторизацию при загрузке страницы
$(document).ready(function() {
    checkAuth();
});