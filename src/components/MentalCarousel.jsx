import { Carousel } from "antd";
import "../css/MentalResource.css";

function MentalCarousel({ images }) {
  return (
    <Carousel autoplay dotPosition="bottom" className="mental-resource-carousel">
      {images.map((img, idx) => (
        <div key={idx}>
          <img
            src={img}
            alt={`slide-${idx}`}
            className="mental-resource-carousel-img"
          />
        </div>
      ))}
    </Carousel>
  );
}

export default MentalCarousel;