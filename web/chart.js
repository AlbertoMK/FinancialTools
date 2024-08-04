document.addEventListener('DOMContentLoaded', () => {
    const svg = d3.select('#line-chart');
    const initialData = JSON.parse(svg.attr('data-points'));
    updateChart(initialData);
});

function updateChart(dataPoints) {
    const svg = d3.select('#line-chart');
    const margin = { top: 80, right: 30, bottom: 20, left: 30 };
    const width = +svg.attr('width') - margin.left - margin.right;
    const height = +svg.attr('height') - margin.top - margin.bottom;

    // Calcular el rango de datos
    const xExtent = d3.extent(dataPoints, d => d[0]);
    const yExtent = d3.extent(dataPoints, d => d[1]);

    const horizontalLineYValue = 0;
    const yMin = Math.min(yExtent[0], horizontalLineYValue);
    const yMax = Math.max(yExtent[1], horizontalLineYValue);

    const xScale = d3.scaleLinear()
        .domain([xExtent[0], xExtent[1]])
        .range([margin.left, width + margin.left]);

    const yScale = d3.scaleLinear()
        .domain([yMax, yMin])
        .range([margin.top, height + margin.top]);

    const line = d3.line()
        .x(d => xScale(d[0]))
        .y(d => yScale(d[1]))
        .curve(d3.curveBasis);

    // Seleccionar la línea existente, si existe
    let path = svg.selectAll('path.line')
        .data([dataPoints]);

    path.enter().append('path')
        .attr('class', 'line')
        .attr('fill', 'none')
        .attr('stroke', 'steelblue')
        .attr('stroke-width', 2)
        .merge(path) // Combine the entering and updating selections
        .transition() // Add transition
        .duration(750) // Duration of the transition
        .attr('d', line); // Update the line path

    // Actualizar la línea horizontal
    const yPosition = yScale(horizontalLineYValue);

    svg.selectAll('line.horizontal')
        .data([horizontalLineYValue])
        .join('line')
        .attr('class', 'horizontal')
        .attr('x1', xScale(d3.min(dataPoints, d => d[0])))
        .attr('x2', xScale(d3.max(dataPoints, d => d[0])))
        .attr('y1', yPosition)
        .attr('y2', yPosition)
        .attr('stroke', 'lightgrey')
        .attr('stroke-width', 1)
        .attr('stroke-dasharray', '5,5');

    // Actualizar la etiqueta
    const label = svg.selectAll('text')
        .data([dataPoints])
        .join('text')
        .attr('font-size', '12')
        .attr('fill', 'steelblue');

    // Reuse the existing `mousemove` and `mouseleave` logic as needed
    svg.on('mousemove', (event) => {
        const [offsetX, offsetY] = d3.pointer(event);
        const yPositionOnCurve = getYPositionAtX(offsetX);
        const yValue = yScale.invert(yPositionOnCurve);

        label.attr('fill', yValue > 0 ? 'lightgreen' : (yValue < 0 ? '#FF6666' : 'lightgrey'))
            .text(`${yValue > 0 ? '+' : ''}${yValue.toFixed(2)}`)
            .attr('x', offsetX - label.node().getBBox().width / 2)
            .attr('y', yPositionOnCurve - 30);
    });

    svg.on('mouseleave', () => {
        label.text('');
    });

    function getYPositionAtX(xCoord) {
        const xValue = xScale.invert(xCoord);
        const path = svg.select('path').node();
        const pathLength = path.getTotalLength();
        const step = pathLength / 100;
        let closestY = 0;
        let closestDistance = Infinity;

        for (let i = 0; i < pathLength; i += step) {
            const point = path.getPointAtLength(i);
            const distance = Math.abs(point.x - xCoord);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestY = point.y;
            }
        }

        return closestY;
    }
}


function updateChartFromButton(data) {
    // Convierte la cadena en un array de datos
    const newData = JSON.parse(data);
    updateChart(newData);
}
