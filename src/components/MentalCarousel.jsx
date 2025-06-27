import { Carousel } from "antd";
import "../css/MentalResource.css";

function MentalCarousel({ images }) {
  return (
    <div className="mental-carousel-wrapper">
      <Carousel 
        autoplay 
        dotPosition="bottom" 
        className="mental-carousel"
        autoplaySpeed={4000}
        effect="fade"
      >
        {images.map((img, idx) => (
          <div key={idx} className="mental-carousel-slide">
            <img
              src={img}
              alt={`心理健康小贴士 ${idx + 1}`}
              className="mental-carousel-image"
            />
            <div className="mental-carousel-overlay">
              <div className="mental-carousel-content">
                <h3>每日心理小贴士</h3>
                <p>关注心理健康，从每一天开始</p>
              </div>
            </div>
          </div>
        ))}
      </Carousel>
    </div>
  );
}

export default MentalCarousel;