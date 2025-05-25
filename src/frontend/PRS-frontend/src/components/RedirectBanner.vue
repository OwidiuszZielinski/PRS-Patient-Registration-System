<template>
  <div class="overlay">
    <div class="banner">
      <p>{{ message }}</p>
      <div class="progress-bar">
        <div class="progress" :style="{ width: progressWidth + '%' }"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'RedirectBanner',
  props: {
    message: { type: String, required: true },
    duration: { type: Number, default: 3000 },
    redirectUrl: { type: String, required: true }
  },
  setup(props) {
    const progressWidth = ref(100)

    onMounted(() => {
      const interval = 50
      const step = (interval / props.duration) * 100
      let width = 100
      const timer = setInterval(() => {
        width -= step
        progressWidth.value = width
        if (width <= 0) {
          clearInterval(timer)
          window.location.href = props.redirectUrl
        }
      }, interval)
    })

    return {progressWidth}
  }
}
</script>

<style scoped>
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 10000;
}

.banner {
  width: 400px;
  background: #5d3997;
  padding: 30px 40px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.4);
  font-size: 1.2rem;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #484951;
  border-radius: 4px;
  margin-top: 16px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background: #42b983;
  transition: width 0.1s linear;
}
</style>
