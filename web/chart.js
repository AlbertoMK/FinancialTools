document.addEventListener('DOMContentLoaded', () => {
    const svg = document.getElementById('line-chart');
    const points = JSON.parse(svg.getAttribute('data-points')); // Leer datos del atributo
    const margin = { top: 80, right: 30, bottom: 0, left: 30 }; // Definir márgenes
    const width = svg.getAttribute('width') - margin.left - margin.right;
    const height = svg.getAttribute('height') - margin.top - margin.bottom;

    // Ajustar el dominio para que el primer punto comience en la esquina inferior izquierda
    const xScale = d3.scaleLinear()
        .domain([d3.min(points, d => d[0]), d3.max(points, d => d[0])])
        .range([30, width]);

    const yScale = d3.scaleLinear()
        .domain([d3.max(points, d => d[1]), d3.min(points, d => d[1])]) // Nota: invertir dominio para que el valor máximo esté arriba
        .range([45, height]);

    const line = d3.line()
        .x(d => xScale(d[0])) // Ajustar la posición X
        .y(d => yScale(d[1])) // Ajustar la posición Y
        .curve(d3.curveBasis);

    // Clear existing content
    svg.innerHTML = '';

    // Create the line
    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    path.setAttribute('d', line(points));
    path.setAttribute('fill', 'none');
    path.setAttribute('stroke', 'steelblue');
    path.setAttribute('stroke-width', 2);
    svg.appendChild(path);

    // Añadir línea horizontal en y=0
    const yValue = 0; // Línea horizontal en y=0
    const yPosition = yScale(yValue);

    const horizontalLine = document.createElementNS('http://www.w3.org/2000/svg', 'line');
    horizontalLine.setAttribute('x1', xScale(d3.min(points, d => d[0])));
    horizontalLine.setAttribute('x2', xScale(d3.max(points, d => d[0])));
    horizontalLine.setAttribute('y1', yPosition);
    horizontalLine.setAttribute('y2', yPosition);
    horizontalLine.setAttribute('stroke', 'lightgrey');
    horizontalLine.setAttribute('stroke-width', 1);
    horizontalLine.setAttribute('stroke-dasharray', '5,5'); // Línea punteada
    svg.appendChild(horizontalLine);

    // Create a text element for displaying the label
    const label = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    label.setAttribute('font-size', '12');
    label.setAttribute('fill', 'steelblue'); // Set text color to steelblue
    svg.appendChild(label);

    // Create a function to get the y position on the curve for a given x position
    function getYPositionAtX(xCoord) {
        const xValue = xScale.invert(xCoord);
        const pathLength = path.getTotalLength();
        const step = pathLength / 100; // Check at 100 intervals
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

        return closestY; // Return y position on path
    }

    // Update label position on mousemove
    svg.addEventListener('mousemove', (event) => {
        const { offsetX, offsetY } = event;
        const yPositionOnCurve = getYPositionAtX(offsetX);
        const yValue = yScale.invert(yPositionOnCurve);

        // Set the label text and position
	if(yValue > 0) {
		label.setAttribute('fill', 'lightgreen');
		label.textContent = `+${yValue.toFixed(2)}`;
	}
	else if(yValue < 0) {
		label.setAttribute('fill', '#FF6666');
		label.textContent = `${yValue.toFixed(2)}`;
	}
	else {
		label.setAttribute('fill', 'lightgrey');
		label.textContent = `+${yValue.toFixed(2)}`;
	}
        label.setAttribute('x', offsetX - label.getBBox().width/2); // La posición X del label
        label.setAttribute('y', yPositionOnCurve - 30); // La posición Y del label
    });

    svg.addEventListener('mouseleave', () => {
        // Hide the label when the mouse leaves the SVG
        label.textContent = '';
    });
});
