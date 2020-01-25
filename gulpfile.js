/**
 * Created by jpereira on 12/19/2016.
 */
// gulp
var gulp = require('gulp');

// plugins
var browserify = require('browserify');
var uglifyify = require('uglifyify');       // browserify transform
var del = require('del');
var connect = require('gulp-connect');
var jshint = require('gulp-jshint');
var cleanCSS = require('gulp-clean-css');
var sourceMaps = require('gulp-sourcemaps');
var rename     = require('gulp-rename');
var livereload = require('gulp-livereload');
var inject     = require('gulp-inject');
var uglify = require('gulp-uglify');
var ngConfig = require('gulp-ng-config');
var pump = require('pump');
var buffer     = require('vinyl-buffer');
var source     = require('vinyl-source-stream');
var series = require('stream-series');
var runSequence = require('run-sequence');
var fs = require('fs');
var argv = require('yargs').argv;

var serverConfig = require('./app/environments');

// build config files
var config = {
    base: {
        outputDir: 'src/main/resources/static/main',
        index: 'app.html'
    },
    js: {
        src: 'app/js/main/app.js',       // Entry point
        outputFile: 'bundle.js', // Name to use for bundle
        outputDir: 'src/main/resources/static/main/js',  // Directory to save bundle to
        mapSubDir: 'maps',      // Subdirectory to save maps to
        translation: {
            outputDir: 'src/main/resources/static/main/translations'
        }
    },
    css: {
        outputDir: 'src/main/resources/static/main/css',
        mapSubDir: 'maps'
    },
    images : {
        outputDir: 'src/main/resources/static/main/images'
    },
    fonts : {
        outputDir: 'src/main/resources/static/main/css/fonts'
    },
    plugins : {
        outputDir: 'src/main/resources/static/main/plugins'
    },
    login: {
        base : {
            outputDir: 'src/main/resources/static',
            index: 'index.html'
        },
        js: {
            src: 'app/js/login/app.js',       // Entry point
            outputFile: 'bundle.js', // Name to use for bundle
            outputDir: 'src/main/resources/static/js',  // Directory to save bundle to
            mapSubDir: 'maps',      // Subdirectory to save maps to
            translation: {
                outputDir: 'src/main/resources/static/translations'
            }
        },
        css: {
            outputDir: 'src/main/resources/static/css',
            mapSubDir: 'maps'
        },
        images : {
            outputDir: 'src/main/resources/static/images'
        },
        fonts : {
            outputDir: 'src/main/resources/static/css/fonts'
        }
    },
};

// This method makes it easy to use common bundling options in different tasks
function bundle (bundler) {

    // Add options to add to "base" bundler passed as parameter
    return bundler
        .bundle()                                       // Start bundle
        .pipe(source(config.js.src))                    // Entry point
        .pipe(buffer())                                 // Convert to gulp pipeline
        .pipe(rename(config.js.outputFile))             // Rename output from 'main.js' to 'bundle.js'
        .pipe(sourceMaps.init({ loadMaps : true }))     // Strip inline source maps
        .pipe(sourceMaps.write(config.js.mapSubDir))    // Save source maps to their own directory
        .pipe(gulp.dest(config.js.outputDir))           // Save 'bundle' to build/
        .pipe(livereload());                            // Reload browser if relevant
}

// tasks
gulp.task('lint', function() {
    return gulp.src(['app/js/**/*.js', '!app/js/login/*.js', '!./app/js/external/**', '!./app/bower_components/**'])
        .pipe(jshint())
        .pipe(jshint.reporter('default'))
        .pipe(jshint.reporter('fail'));
});

gulp.task('browserify', function() {
    var bundler = browserify(config.js.src, {debug: true});  // Pass browserify the entry point
    bundler.transform({
        global: true
    }, 'uglifyify');

    return  bundle(bundler)  // Chain other options -- sourcemaps, rename, etc.
});

gulp.task('minify-css', function() {
    return  gulp.src(['app/css/**/*.css'])
        .pipe(sourceMaps.init({ loadMaps : true }))     // Strip inline source maps
        .pipe(cleanCSS())
        .pipe(sourceMaps.write(config.css.mapSubDir))    // Save source maps to their own directory
        .pipe(gulp.dest(config.css.outputDir));

});

gulp.task('external-js', function () {
    return pump([
            gulp.src('app/js/external/**/*.js'),
            uglify(),
            gulp.dest(config.base.outputDir + '/js/external')
        ]
    );
});

gulp.task('bower-components', function () {
    return  gulp.src('app/js/bower_components/**')
        .pipe(gulp.dest(config.base.outputDir + '/js/bower_components'));
});

// Html
gulp.task('html', function () {
    return gulp.src(['app/partials/main/**/**.html']) // do not include the index file, it will be handled by the inject task
        .pipe(gulp.dest(config.base.outputDir + '/partials'));
});

// Html
gulp.task('html-templates', function () {
    return gulp.src(['app/partials/templates/**.html']) // do not include the index file, it will be handled by the inject task
        .pipe(gulp.dest('src/main/resources/static'));
});


// Fonts
gulp.task('fonts', function() {
    return gulp.src([
            'app/fonts/*.*'])
        .pipe(gulp.dest(config.fonts.outputDir));
});

// Images
gulp.task('images', function() {
    return gulp.src([
            'app/images/*.*'])
        .pipe(gulp.dest(config.images.outputDir));
});

gulp.task('plugins', function() {
    return gulp.src([
            'app/plugins/**'])
        .pipe(gulp.dest(config.plugins.outputDir));
});

gulp.task('inject',  function(){
    var jsTranslateSources = gulp.src(config.js.translation.outputDir + '/**/*.js', {read : false});
    var jsSources = gulp.src(config.js.outputDir + '/**/*.js', {read : false});
    var vendorCssSources = gulp.src(config.css.outputDir + '/*.css', {read : false});
    var externalCssSources = gulp.src(config.css.outputDir + '/external/**/*.css', {read : false});

    return gulp.src('app/' + config.base.index)
        .pipe(inject(series(jsTranslateSources, jsSources, externalCssSources, vendorCssSources), { ignorePath: "src/main/resources/static", addRootSlash: false }))
        .pipe(gulp.dest(config.base.outputDir));
});

gulp.task('watch', function() {
    gulp.watch('app/js/**/*.js', {verbose : true, delay : 3000}, ['browserify']);
    gulp.watch('app/css/**/*.css', {verbose : true, delay : 3000}, ['minify-css']);
    gulp.watch('app/images/*.*', {verbose : true, delay : 3000}, ['images']);
    gulp.watch('app/**/*.html', {verbose : true, delay : 3000}, ['inject', 'html']);
});

gulp.task('main-build', function() {
    runSequence(
        ['browserify', 'minify-css', 'external-js'],
        ['inject'],
        ['html', 'html-templates', 'images', 'fonts', 'plugins']
    );
});

/* UM */
// This method makes it easy to use common bundling options in different tasks
function umBundle (bundler) {

    // Add options to add to "base" bundler passed as parameter
    return bundler
        .bundle()                                       // Start bundle
        .pipe(source(config.login.js.src))                    // Entry point
        .pipe(buffer())                                 // Convert to gulp pipeline
        .pipe(rename(config.login.js.outputFile))             // Rename output from 'main.js' to 'bundle.js'
        .pipe(sourceMaps.init({ loadMaps : true }))     // Strip inline source maps
        .pipe(sourceMaps.write(config.login.js.mapSubDir))    // Save source maps to their own directory
        .pipe(gulp.dest(config.login.js.outputDir))           // Save 'bundle' to build/
        .pipe(livereload());                            // Reload browser if relevant
}

// CSS
gulp.task('login-minify-css', function() {
    return  gulp.src(['app/css/**/*.css'])
        .pipe(sourceMaps.init({ loadMaps : true }))     // Strip inline source maps
        .pipe(cleanCSS())
        .pipe(sourceMaps.write(config.login.css.mapSubDir))    // Save source maps to their own directory
        .pipe(gulp.dest(config.login.css.outputDir));

});

// Html
gulp.task('login-html', function () {
    return gulp.src(['app/**/*.html', '!app/**/' + config.base.index]) // do not include the index file, it will be handled by the inject task
        .pipe(gulp.dest(config.login.base.outputDir));
});

// Fonts
gulp.task('login-fonts', function() {
    return gulp.src([
            'app/fonts/*.*'])
        .pipe(gulp.dest(config.login.fonts.outputDir));
});

// Images
gulp.task('login-images', function() {
    return gulp.src([
            'app/images/login/*.*'])
        .pipe(gulp.dest(config.login.images.outputDir));
});

gulp.task('login-browserify', function() {
    var bundler = browserify(config.login.js.src, {debug: true});  // Pass browserify the entry point
    bundler.transform({
        global: true
    }, 'uglifyify');

    return umBundle(bundler)  // Chain other options -- sourcemaps, rename, etc.
});

gulp.task('login-inject',  function(){
    var jsTranslateSources = gulp.src(config.login.js.translation.outputDir + '/**/*.js', {read : false});
    var jsSources = gulp.src(config.login.js.outputDir + '/**/*.js', {read : false});
    var vendorCssSources = gulp.src(config.login.css.outputDir + '/**/*.css', {read : false});

    return gulp.src('app/' + config.login.base.index)
        .pipe(inject(series(jsTranslateSources, jsSources, vendorCssSources), {ignorePath: config.login.base.outputDir, addRootSlash: false}))
        .pipe(gulp.dest(config.login.base.outputDir));
});

gulp.task('login-external-js', function () {
    return pump([
            gulp.src('app/js/external/**/*.js'),
            uglify(),
            gulp.dest(config.login.js.outputDir + '/external')
        ]
    );
});

gulp.task('login-watch', function() {
    gulp.watch('app/js/**/*.js', {verbose : true, delay : 3000}, ['login-browserify']);
    gulp.watch('app/css/**/*.css', {verbose : true, delay : 3000}, ['login-minify-css']);
    gulp.watch('app/images/*.*', {verbose : true, delay : 3000}, ['login-images']);
    gulp.watch('app/**/*.html', {verbose : true, delay : 3000}, ['login-inject']);
});

gulp.task('login-clean', function() {
    return del([config.login.base.outputDir + '/index.html', config.login.js.outputDir + '/**', config.login.images.outputDir + '/**',  config.login.css.outputDir + '/**']);
});

gulp.task('login-build', function() {
    runSequence(
        ['login-clean', 'env-config'],
        ['login-browserify', 'login-minify-css', 'login-images', 'login-fonts'],
        ['login-external-js'],
        ['login-inject']
    );
});

// general tasks
gulp.task('default',
    ['connect']
);

gulp.task('env-config', function() {
    var configJsonName = './env-config.json';

    var env = argv.env || 'development';
    console.log("Environment is " + env);

    fs.writeFileSync(configJsonName, JSON.stringify(serverConfig[env]));

    gulp.src(configJsonName)
        .pipe(ngConfig('baseApp.envConfig', {
            createModule : true
        }))
        .pipe(rename('config.js'))
        .pipe(gulp.dest('app/js/'));

    return del(configJsonName);
});

gulp.task('connect', ['build', 'watch', 'login-watch'], function () {
    connect.server({
        root: config.base.outputDir,
        port: 8888
    })
});

gulp.task('clean', function() {
    return del(config.login.base.outputDir + '/**');
});

gulp.task('build', function() {
    runSequence(
        ['clean', 'env-config'],
        ['main-build', 'login-build']
    );
});