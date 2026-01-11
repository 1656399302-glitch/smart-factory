function cubicBezier(x1, y1, x2, y2) {
  return function(t) {
    if (t <= 0) return 0
    if (t >= 1) return 1
    
    const cx = 3 * x1
    const bx = 3 * (x2 - x1) - cx
    const ax = 1 - cx - bx
    
    const cy = 3 * y1
    const by = 3 * (y2 - y1) - cy
    const ay = 1 - cy - by
    
    function sampleCurveX(t) {
      return ((ax * t + bx) * t + cx) * t
    }
    
    function sampleCurveY(t) {
      return ((ay * t + by) * t + cy) * t
    }
    
    function solve(x, epsilon) {
      let t0, t1, t2, x2, d2, i
      for (t2 = x, i = 0; i < 8; i++) {
        x2 = sampleCurveX(t2) - x
        if (Math.abs(x2) < epsilon) return sampleCurveY(t2)
        d2 = (6 * ax * t2 + 2 * bx) * t2 + cx
        if (Math.abs(d2) < 1e-6) break
        t2 = t2 - x2 / d2
      }
      t0 = 0
      t1 = 1
      t2 = x
      if (t2 < t0) return sampleCurveY(t0)
      if (t2 > t1) return sampleCurveY(t1)
      while (t0 < t1) {
        x2 = sampleCurveX(t2)
        if (Math.abs(x2 - x) < epsilon) return sampleCurveY(t2)
        if (x > x2) t0 = t2
        else t1 = t2
        t2 = (t1 - t0) * 0.5 + t0
      }
      return sampleCurveY(t2)
    }
    
    return solve(t, 0.0001)
  }
}

const easingStandard = cubicBezier(0.2, 0.8, 0.2, 1)

const requestAnimFrame = (function() {
  return window.requestAnimationFrame || 
         window.webkitRequestAnimationFrame || 
         window.mozRequestAnimationFrame || 
         function(callback) { 
           window.setTimeout(callback, 1000 / 60) 
         }
})()

function getScrollPosition() {
  return window.pageYOffset || 
         document.documentElement.scrollTop || 
         document.body.scrollTop || 0
}

function setScrollPosition(position) {
  window.scrollTo(0, position)
}

export function smoothScrollTo(target, duration = 350) {
  const start = getScrollPosition()
  let targetPosition
  
  if (typeof target === 'string') {
    const element = document.querySelector(target)
    if (!element) return Promise.resolve()
    targetPosition = element.getBoundingClientRect().top + start
  } else if (typeof target === 'number') {
    targetPosition = target
  } else if (target && target.getBoundingClientRect) {
    targetPosition = target.getBoundingClientRect().top + start
  } else {
    return Promise.resolve()
  }
  
  const distance = targetPosition - start
  if (Math.abs(distance) < 1) {
    return Promise.resolve()
  }
  
  return new Promise((resolve) => {
    let startTime = null
    
    function animate(currentTime) {
      if (startTime === null) startTime = currentTime
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)
      
      const eased = easingStandard(progress)
      const currentPosition = start + distance * eased
      
      setScrollPosition(currentPosition)
      
      if (progress < 1) {
        requestAnimFrame(animate)
      } else {
        resolve()
      }
    }
    
    requestAnimFrame(animate)
  })
}

export function initSmoothAnchorScroll() {
  document.addEventListener('click', (e) => {
    const link = e.target.closest('a[href^="#"]')
    if (!link) return
    
    const href = link.getAttribute('href')
    if (href === '#' || href === '#!') return
    
    const target = document.querySelector(href)
    if (!target) return
    
    e.preventDefault()
    smoothScrollTo(target, 350)
  }, { passive: false })
}


