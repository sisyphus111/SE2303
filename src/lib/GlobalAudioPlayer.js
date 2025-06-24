import React, { createContext, useContext, useRef, useState } from "react";
import "../css/MentalResource.css";


const AudioContext = createContext();

// 全局状态管理，在任何组件用 const { play } = useAudio();，然后 play(music) 即可全局播放
export function useAudio(){
    return useContext(AudioContext);
}

export function AudioProvider({ children }) {
  const audioRef = useRef(null);
  const [current, setCurrent] = useState(null);

  // 设置当前播放的音乐，并自动播放
  const play = (music) => {
    setCurrent(music);
    setTimeout(() => {
      audioRef.current && audioRef.current.play();
    }, 0);
  };


  return (
    <AudioContext.Provider value={{ current, play }}>
      {children}
      <div className={`global-audio-player-bar${current ? "" : " hide"}`}>
        {current && (
          <>
            <img src={current.cover} alt="" className="global-audio-player-cover" />
            <div className="global-audio-player-info">
              <div className="global-audio-player-title">{current.title}</div>
              <div className="global-audio-player-artist">{current.artist}</div>
            </div>
            <audio
              ref={audioRef}
              src={current.url}
              controls
              autoPlay
              className="global-audio-player-audio"
            />
          </>
        )}
      </div>
    </AudioContext.Provider>
  );
}