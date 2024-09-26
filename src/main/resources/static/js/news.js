document.getElementById('load-more').addEventListener('click', function() {
    const hiddenNews = document.querySelectorAll('#hidden-news.d-none'); // 모든 숨겨진 뉴스 요소 선택
    const itemsToShow = 5; // 한 번에 보여줄 뉴스 개수

    for (let i = 0; i < Math.min(itemsToShow, hiddenNews.length); i++) {
        hiddenNews[i].classList.remove('d-none'); // 숨겨진 뉴스 요소를 보이게 함
    }

    // 숨겨진 뉴스가 더 이상 없으면 버튼 숨김
    if (hiddenNews.length <= itemsToShow) {
        this.classList.add('d-none');
    }
});


$(document).ready(function() {
    $('.responsive').slick({
        dots: true,
        infinite: false,
        speed: 300,
        slidesToShow: 3,
        slidesToScroll: 1,
        prevArrow: '<button type="button" class="slick-prev">이전</button>',
        nextArrow: '<button type="button" class="slick-next">다음</button>',
        responsive: [
            {
                breakpoint: 1024,
                settings: {
                    slidesToShow: 2,
                    slidesToScroll: 1,
                }
            },
            {
                breakpoint: 600,
                settings: {
                    slidesToShow: 1,
                    slidesToScroll: 1,
                }
            }
        ]
    });
});