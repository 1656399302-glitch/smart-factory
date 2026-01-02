const observerOptions = {
  root: null,
  rootMargin: '0px',
  threshold: 0.1
}

let observer = null

function initObserver() {
  if (typeof IntersectionObserver === 'undefined') {
    return null
  }

  return new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible')
      }
    })
  }, observerOptions)
}

function getObserver() {
  if (!observer) {
    observer = initObserver()
  }
  return observer
}

export default {
  inserted(el) {
    if (!el.classList.contains('u-reveal')) {
      el.classList.add('u-reveal')
    }

    const obs = getObserver()
    if (obs) {
      obs.observe(el)
    } else {
      el.classList.add('is-visible')
    }
  },

  unbind(el) {
    const obs = getObserver()
    if (obs) {
      obs.unobserve(el)
    }
  }
}

