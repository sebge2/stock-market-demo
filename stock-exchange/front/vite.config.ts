import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { nxViteTsPaths } from '@nx/vite/plugins/nx-tsconfig-paths.plugin';

export default defineConfig({
  plugins: [nxViteTsPaths(), react()],
  server: {
    port: 3001,
    host: 'localhost',
    strictPort: false,
    fs: {
      allow: ['..', '../..'],
    },
  },
  build: {
    outDir: '../../dist/stock-exchange/front',
    reportCompressedSize: true,
    commonjsOptions: {
      transformMixedEsModules: true,
    },
  },
});
