import React from 'react';

const LiveBadge = ({ status = 'live' }) => {
  const isLive = status === 'live';

  return (
    <div className="flex items-center px-3 py-1 border border-gray-300 rounded-full bg-white shadow-sm justify-center w-fit">
      <div className="text-sm font-bold text-gray-800 mr-2 leading-normal">
        {isLive ? 'LIVE' : 'FAILURE'}
      </div>
      <div
        className={`w-2.5 h-2.5 rounded-full ${
          isLive ? 'bg-red-500 animate-pulse' : 'bg-gray-500'
        }`}
      ></div>
    </div>
  );
};

export default LiveBadge;
