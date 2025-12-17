// Функция для переключения состояния избранного
function toggleFavorite(button) {
    const productId = button.getAttribute('data-product-id');
    const icon = button.querySelector('i');

    // Проверяем текущее состояние
    const isFavorite = icon.classList.contains('bi-heart-fill');

    if (isFavorite) {
        // Удаляем из избранного
        fetch('/favorites/remove/' + productId, {
            method: 'POST'
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = '/login';
                return;
            }
            if (response.ok) {
                icon.classList.remove('bi-heart-fill', 'text-danger');
                icon.classList.add('bi-heart', 'text-muted');
                updateFavoriteCount();
            } else {
                alert('Ошибка при удалении из избранного');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Произошла ошибка');
        });
    } else {
        // Добавляем в избранное
        fetch('/favorites/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'productId=' + productId
        })
        .then(response => response.text())
        .then(result => {
            if (result === 'success') {
                icon.classList.remove('bi-heart', 'text-muted');
                icon.classList.add('bi-heart-fill', 'text-danger');
                updateFavoriteCount();
            } else if (result.startsWith('error:not_logged_in')) {
                window.location.href = '/login';
            } else {
                alert('Ошибка при добавлении в избранное');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Произошла ошибка');
        });
    }
}

// Обновить счетчик избранного
function updateFavoriteCount() {
    fetch('/favorites/count')
        .then(response => response.json())
        .then(count => {
            const favoriteBadge = document.querySelector('#favorite-count-badge');
            if (favoriteBadge) {
                favoriteBadge.textContent = count;
                if (count > 0) {
                    favoriteBadge.classList.remove('d-none');
                } else {
                    favoriteBadge.classList.add('d-none');
                }
            }
        })
        .catch(error => console.error('Error updating favorite count:', error));
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    // Проверяем статус избранного для всех товаров на странице
    const favoriteButtons = document.querySelectorAll('[data-product-id]');

    favoriteButtons.forEach(button => {
        const productId = button.getAttribute('data-product-id');
        const icon = button.querySelector('i');

        fetch('/favorites/check?productId=' + productId)
            .then(response => response.text())
            .then(status => {
                if (status === 'favorite') {
                    icon.classList.remove('bi-heart', 'text-muted');
                    icon.classList.add('bi-heart-fill', 'text-danger');
                } else if (status === 'not_favorite') {
                    icon.classList.remove('bi-heart-fill', 'text-danger');
                    icon.classList.add('bi-heart', 'text-muted');
                }
            })
            .catch(error => console.error('Error checking favorite:', error));
    });
});